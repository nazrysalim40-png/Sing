package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.SongReleaseDatabase
import com.example.data.model.ChecklistCategory
import com.example.data.model.PromoCampaignTask
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.ReleaseStatus
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.data.repository.SongReleaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReleaseUiStats(
    val totalChecklistCount: Int = 0,
    val completedChecklistCount: Int = 0,
    val completionPercentage: Int = 0,
    val splitTotalPercentage: Double = 0.0,
    val isSplitSheetValid: Boolean = false,
    val promoCompletedCount: Int = 0,
    val promoTotalCount: Int = 0,
    val daysUntilRelease: Long = 0,
    val hoursRemaining: Long = 0,
    val isDropped: Boolean = false
)

class SongReleaseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SongReleaseRepository

    init {
        val database = SongReleaseDatabase.getDatabase(application)
        repository = SongReleaseRepository(database)
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    val allReleases: StateFlow<List<SongRelease>> = repository.allReleases
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSongs: StateFlow<List<Song>> = repository.allSongs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedReleaseId = MutableStateFlow<Long?>(null)
    val selectedReleaseId: StateFlow<Long?> = _selectedReleaseId.asStateFlow()

    private val _selectedChecklistCategory = MutableStateFlow<ChecklistCategory?>(null)
    val selectedChecklistCategory: StateFlow<ChecklistCategory?> = _selectedChecklistCategory.asStateFlow()

    // Current active release
    val currentRelease: StateFlow<SongRelease?> = combine(
        allReleases,
        _selectedReleaseId
    ) { releases, selectedId ->
        if (releases.isEmpty()) null
        else if (selectedId != null) {
            releases.find { it.id == selectedId } ?: releases.firstOrNull()
        } else {
            releases.firstOrNull()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Current song entity matching active release
    val currentSong: StateFlow<Song?> = currentRelease
        .flatMapLatest { release ->
            if (release == null) flowOf(null)
            else repository.getSongByReleaseId(release.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Current checklist items
    val currentChecklist: StateFlow<List<ReleaseChecklistItem>> = currentRelease
        .flatMapLatest { release ->
            if (release == null) flowOf(emptyList())
            else repository.getChecklistForRelease(release.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current split sheets
    val currentSplits: StateFlow<List<SongWriterSplit>> = currentRelease
        .flatMapLatest { release ->
            if (release == null) flowOf(emptyList())
            else repository.getSplitsForRelease(release.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current promo tasks
    val currentPromoTasks: StateFlow<List<PromoCampaignTask>> = currentRelease
        .flatMapLatest { release ->
            if (release == null) flowOf(emptyList())
            else repository.getPromoTasksForRelease(release.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current stats & readiness computation
    val releaseStats: StateFlow<ReleaseUiStats> = combine(
        currentRelease,
        currentChecklist,
        currentSplits,
        currentPromoTasks
    ) { release, checklist, splits, promoTasks ->
        if (release == null) return@combine ReleaseUiStats()

        val totalChecklist = checklist.size
        val completedChecklist = checklist.count { it.isCompleted }
        val completionPct = if (totalChecklist > 0) (completedChecklist * 100) / totalChecklist else 0

        val totalSplit = splits.sumOf { it.percentage }
        val isSplitValid = Math.abs(totalSplit - 100.0) < 0.01

        val promoDone = promoTasks.count { it.isDone }
        val promoTotal = promoTasks.size

        val now = System.currentTimeMillis()
        val diffMillis = release.releaseDateMillis - now
        val daysUntil = if (diffMillis > 0) diffMillis / (1000 * 60 * 60 * 24) else 0
        val hoursRem = if (diffMillis > 0) (diffMillis / (1000 * 60 * 60)) % 24 else 0
        val isDropped = release.status == ReleaseStatus.DROPPED || diffMillis <= 0

        ReleaseUiStats(
            totalChecklistCount = totalChecklist,
            completedChecklistCount = completedChecklist,
            completionPercentage = completionPct,
            splitTotalPercentage = totalSplit,
            isSplitSheetValid = isSplitValid,
            promoCompletedCount = promoDone,
            promoTotalCount = promoTotal,
            daysUntilRelease = daysUntil,
            hoursRemaining = hoursRem,
            isDropped = isDropped
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReleaseUiStats()
    )

    fun selectRelease(id: Long) {
        _selectedReleaseId.value = id
    }

    fun selectChecklistCategory(category: ChecklistCategory?) {
        _selectedChecklistCategory.value = category
    }

    fun toggleChecklistItem(item: ReleaseChecklistItem) {
        viewModelScope.launch {
            repository.updateChecklistItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun addChecklistItem(category: ChecklistCategory, title: String, description: String, offsetDays: Int = 0) {
        val release = currentRelease.value ?: return
        viewModelScope.launch {
            val newItem = ReleaseChecklistItem(
                releaseId = release.id,
                category = category,
                title = title.trim(),
                description = description.trim(),
                isCompleted = false,
                milestoneDaysOffset = offsetDays
            )
            repository.insertChecklistItem(newItem)
        }
    }

    fun deleteChecklistItem(item: ReleaseChecklistItem) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }

    fun updateReleaseMetadata(
        title: String,
        artistName: String,
        featuredArtists: String,
        genre: String,
        subGenre: String,
        bpm: Int,
        musicalKey: String,
        isrcCode: String,
        upcCode: String,
        distributor: String,
        releaseDateMillis: Long,
        explicitLyrics: Boolean,
        preSaveUrl: String,
        dspSpotifyUrl: String,
        dspAppleMusicUrl: String,
        lyrics: String,
        pitchBlurb: String,
        coverArtPreset: String
    ) {
        val current = currentRelease.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                title = title.trim(),
                artistName = artistName.trim(),
                featuredArtists = featuredArtists.trim(),
                genre = genre.trim(),
                subGenre = subGenre.trim(),
                bpm = bpm,
                musicalKey = musicalKey.trim(),
                isrcCode = isrcCode.trim(),
                upcCode = upcCode.trim(),
                distributor = distributor.trim(),
                releaseDateMillis = releaseDateMillis,
                explicitLyrics = explicitLyrics,
                preSaveUrl = preSaveUrl.trim(),
                dspSpotifyUrl = dspSpotifyUrl.trim(),
                dspAppleMusicUrl = dspAppleMusicUrl.trim(),
                lyrics = lyrics.trim(),
                pitchBlurb = pitchBlurb.trim(),
                coverArtPreset = coverArtPreset
            )
            repository.updateRelease(updated)
        }
    }

    fun updateReleaseStatus(newStatus: ReleaseStatus) {
        val current = currentRelease.value ?: return
        viewModelScope.launch {
            repository.updateRelease(current.copy(status = newStatus))
        }
    }

    fun createNewRelease(
        title: String,
        artistName: String,
        featuredArtists: String,
        genre: String,
        subGenre: String,
        releaseDateMillis: Long,
        bpm: Int,
        musicalKey: String,
        distributor: String,
        coverArtPreset: String,
        pitchBlurb: String
    ) {
        viewModelScope.launch {
            val newId = repository.createReleaseWithDefaults(
                title = title,
                artistName = artistName,
                featuredArtists = featuredArtists,
                genre = genre,
                subGenre = subGenre,
                releaseDateMillis = releaseDateMillis,
                bpm = bpm,
                musicalKey = musicalKey,
                distributor = distributor,
                coverArtPreset = coverArtPreset,
                pitchBlurb = pitchBlurb
            )
            _selectedReleaseId.value = newId
        }
    }

    fun deleteRelease(releaseId: Long) {
        viewModelScope.launch {
            repository.deleteRelease(releaseId)
            _selectedReleaseId.value = null
        }
    }

    fun addSplit(name: String, role: String, percentage: Double, pro: String, ipi: String) {
        val release = currentRelease.value ?: return
        viewModelScope.launch {
            val split = SongWriterSplit(
                releaseId = release.id,
                contributorName = name.trim(),
                role = role.trim(),
                percentage = percentage,
                proAffiliation = pro.trim(),
                ipiNumber = ipi.trim()
            )
            repository.insertSplit(split)
        }
    }

    fun updateSplit(split: SongWriterSplit) {
        viewModelScope.launch {
            repository.updateSplit(split)
        }
    }

    fun deleteSplit(split: SongWriterSplit) {
        viewModelScope.launch {
            repository.deleteSplit(split)
        }
    }

    fun saveSplitSheet(splits: List<SongWriterSplit>) {
        val current = currentRelease.value ?: return
        viewModelScope.launch {
            repository.updateSplitsForRelease(current.id, splits)
        }
    }

    fun addPromoTask(platform: String, title: String, contentIdea: String, scheduledDateMillis: Long, cost: Double) {
        val release = currentRelease.value ?: return
        viewModelScope.launch {
            val task = PromoCampaignTask(
                releaseId = release.id,
                platform = platform.trim(),
                title = title.trim(),
                contentIdea = contentIdea.trim(),
                scheduledDateMillis = scheduledDateMillis,
                cost = cost
            )
            repository.insertPromoTask(task)
        }
    }

    fun togglePromoTask(task: PromoCampaignTask) {
        viewModelScope.launch {
            repository.updatePromoTask(task.copy(isDone = !task.isDone))
        }
    }

    fun deletePromoTask(task: PromoCampaignTask) {
        viewModelScope.launch {
            repository.deletePromoTask(task)
        }
    }

    fun saveTrackMetadataFormData(
        title: String,
        isrcCode: String,
        releaseDateMillis: Long,
        songwriterCredits: List<SongWriterSplit>
    ) {
        val current = currentRelease.value ?: return
        viewModelScope.launch {
            repository.updateEssentialMetadataAndSplits(
                releaseId = current.id,
                title = title,
                isrcCode = isrcCode,
                releaseDateMillis = releaseDateMillis,
                splits = songwriterCredits
            )
        }
    }

    fun insertSong(song: Song) {
        viewModelScope.launch {
            repository.insertSong(song)
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch {
            repository.updateSong(song)
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            repository.deleteSong(song)
        }
    }

    fun triggerSimulatedDrop() {
        val current = currentRelease.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                status = ReleaseStatus.DROPPED,
                dspSpotifyUrl = "https://open.spotify.com/track/release_${current.id}",
                dspAppleMusicUrl = "https://music.apple.com/album/release_${current.id}",
                distributorSubmitted = true,
                spotifyEditorialPitched = true
            )
            repository.updateRelease(updated)
        }
    }
}
