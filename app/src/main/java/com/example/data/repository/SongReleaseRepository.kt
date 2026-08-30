package com.example.data.repository

import com.example.data.database.SongReleaseDatabase
import com.example.data.model.ChecklistCategory
import com.example.data.model.PromoCampaignTask
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.ReleaseStatus
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SongReleaseRepository(private val database: SongReleaseDatabase) {
    private val songDao = database.songDao()
    private val songReleaseDao = database.songReleaseDao()
    private val checklistDao = database.releaseChecklistDao()
    private val splitSheetDao = database.splitSheetDao()
    private val promoTaskDao = database.promoTaskDao()

    val allReleases: Flow<List<SongRelease>> = songReleaseDao.getAllReleases()
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()

    fun getReleaseById(id: Long): Flow<SongRelease?> = songReleaseDao.getReleaseById(id)
    fun getSongById(id: Long): Flow<Song?> = songDao.getSongById(id)
    fun getSongByReleaseId(releaseId: Long): Flow<Song?> = songDao.getSongByReleaseId(releaseId)
    suspend fun getSongByReleaseIdDirect(releaseId: Long): Song? = songDao.getSongByReleaseIdDirect(releaseId)

    fun getChecklistForRelease(releaseId: Long): Flow<List<ReleaseChecklistItem>> =
        checklistDao.getChecklistForRelease(releaseId)

    fun getSplitsForRelease(releaseId: Long): Flow<List<SongWriterSplit>> =
        splitSheetDao.getSplitsForRelease(releaseId)

    fun getPromoTasksForRelease(releaseId: Long): Flow<List<PromoCampaignTask>> =
        promoTaskDao.getTasksForRelease(releaseId)

    suspend fun insertSong(song: Song): Long = songDao.insertSong(song)
    suspend fun updateSong(song: Song) = songDao.updateSong(song)
    suspend fun deleteSong(song: Song) = songDao.deleteSong(song)
    suspend fun deleteSongById(id: Long) = songDao.deleteSongById(id)

    suspend fun createReleaseWithDefaults(
        title: String,
        artistName: String,
        featuredArtists: String = "",
        genre: String = "Pop",
        subGenre: String = "Synthwave",
        releaseDateMillis: Long = System.currentTimeMillis() + (21L * 24 * 60 * 60 * 1000), // 3 weeks out
        bpm: Int = 124,
        musicalKey: String = "F# Minor",
        distributor: String = "DistroKid",
        coverArtPreset: String = "neon",
        pitchBlurb: String = ""
    ): Long {
        val release = SongRelease(
            title = title,
            artistName = artistName,
            featuredArtists = featuredArtists,
            genre = genre,
            subGenre = subGenre,
            releaseDateMillis = releaseDateMillis,
            status = ReleaseStatus.SCHEDULED,
            isrcCode = generateSampleIsrc(),
            upcCode = generateSampleUpc(),
            bpm = bpm,
            musicalKey = musicalKey,
            distributor = distributor,
            coverArtPreset = coverArtPreset,
            pitchBlurb = pitchBlurb.ifBlank {
                "\"$title\" is a high-energy $genre track produced by $artistName, blending infectious vocal melodies with modern atmospheric production, crafted for late-night drives and workout playlists."
            },
            preSaveUrl = "https://distrokid.com/hyperfollow/$artistName/${title.lowercase().replace(" ", "")}"
        )

        val releaseId = songReleaseDao.insertRelease(release)

        // Seed default comprehensive checklist
        val defaultChecklist = listOf(
            // Audio & Masters
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.AUDIO_MASTER,
                title = "Final Mixdown Approval",
                description = "Test balanced mix across studio monitors, AirPods, car stereo, and phone speakers.",
                milestoneDaysOffset = -28
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.AUDIO_MASTER,
                title = "24-bit 48kHz WAV Master Export",
                description = "Deliver uncompressed high-res master file with true peak headroom (-1.0 dBTP).",
                milestoneDaysOffset = -25
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.AUDIO_MASTER,
                title = "Streaming Loudness Check (-14 LUFS)",
                description = "Ensure integrated loudness complies with Spotify & Apple Music normalization.",
                milestoneDaysOffset = -24
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.AUDIO_MASTER,
                title = "Export Instrumental, TV Track & Acapella Stems",
                description = "Archive clean stems for sync licensing, remixers, and live performance sets.",
                milestoneDaysOffset = -21
            ),

            // Visuals & Artwork
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.ARTWORK_ASSETS,
                title = "3000 x 3000 px High-Res Cover Art",
                description = "Exact square RGB artwork. No barcodes, website URLs, or streaming platform logos.",
                milestoneDaysOffset = -25
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.ARTWORK_ASSETS,
                title = "Spotify 9:16 Looping Canvas Video",
                description = "Create a captivating 3-8 second vertical loop to drive 145% more track shares.",
                milestoneDaysOffset = -18
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.ARTWORK_ASSETS,
                title = "Social Media Teaser Banner Pack",
                description = "Render 1:1 Instagram grid, 9:16 Story/Reel, and 16:9 YouTube banner graphics.",
                milestoneDaysOffset = -14
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.ARTWORK_ASSETS,
                title = "Official Lyric Video / Visualizer",
                description = "Produce synchronized lyric video for YouTube launch day premiere.",
                milestoneDaysOffset = -10
            ),

            // Metadata & Legal
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.METADATA_LEGAL,
                title = "Sign Split Sheet (100% Total)",
                description = "Confirm master and publishing shares with all co-writers, producers, and vocalists.",
                milestoneDaysOffset = -28
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.METADATA_LEGAL,
                title = "Verify ISRC & UPC Codes",
                description = "Assign unique tracking code for digital royalties and stream monitoring.",
                milestoneDaysOffset = -25
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.METADATA_LEGAL,
                title = "Register with PRO (ASCAP / BMI / PRS)",
                description = "Register song title and splits with your Performance Rights Organization.",
                milestoneDaysOffset = -20
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.METADATA_LEGAL,
                title = "Sample Clearance & Producer Agreements",
                description = "Verify all royalty-free loops or obtain signed clearances for samples used.",
                milestoneDaysOffset = -22
            ),

            // Distribution & Pitching
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DISTRIBUTION,
                title = "Upload to Distributor (4 Weeks Lead Time)",
                description = "Submit audio, metadata, and scheduled date to ${distributor}.",
                milestoneDaysOffset = -28
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DISTRIBUTION,
                title = "Pitch to Spotify for Artists Editorial",
                description = "Select genre tags, mood, instruments, and write compelling editorial pitch story.",
                milestoneDaysOffset = -14
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DISTRIBUTION,
                title = "Submit Apple Music & Amazon Pitch",
                description = "Update artist profile photos and submit release highlight in artist portals.",
                milestoneDaysOffset = -12
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DISTRIBUTION,
                title = "Setup Pre-Save Smart Link",
                description = "Create and test multi-platform pre-save landing page.",
                milestoneDaysOffset = -14
            ),

            // Marketing & Teasers
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.MARKETING_PROMO,
                title = "Behind-The-Scenes Studio Teaser (TikTok/Reels)",
                description = "Post engaging 15-second studio recording clip or beat-making breakdown.",
                milestoneDaysOffset = -14
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.MARKETING_PROMO,
                title = "Cover Art Reveal & Pre-Save Push",
                description = "Post official artwork announcement with direct link in bio.",
                milestoneDaysOffset = -7
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.MARKETING_PROMO,
                title = "Send Electronic Press Kit (EPK) to Indie Blogs",
                description = "Pitch music bloggers, YouTube tastemakers, and genre playlist curators.",
                milestoneDaysOffset = -7
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.MARKETING_PROMO,
                title = "Drop Day Eve Countdown Story",
                description = "Post 24-hour countdown sticker with audio snippet preview.",
                milestoneDaysOffset = -1
            ),

            // Release Day
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DROP_DAY,
                title = "Verify Live Stream on All Major DSPs",
                description = "Check Spotify, Apple Music, YouTube, Amazon, Tidal, and Deezer at midnight.",
                milestoneDaysOffset = 0
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DROP_DAY,
                title = "Switch Smart Link from Pre-Save to Live Streaming",
                description = "Update link in bio to direct users to their preferred streaming service.",
                milestoneDaysOffset = 0
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DROP_DAY,
                title = "Set as Spotify 'Artist Pick'",
                description = "Pin your new track with a custom launch message at the top of your profile.",
                milestoneDaysOffset = 0
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DROP_DAY,
                title = "Release Day Social Announcement Blast",
                description = "Publish high-energy video drop announcement across all channels.",
                milestoneDaysOffset = 0
            ),
            ReleaseChecklistItem(
                releaseId = releaseId,
                category = ChecklistCategory.DROP_DAY,
                title = "Send Launch Email to Fan Community",
                description = "Notify loyal listeners with streaming links and story behind the song.",
                milestoneDaysOffset = 0
            )
        )
        checklistDao.insertChecklistItems(defaultChecklist)

        // Seed default split sheet
        val defaultSplits = listOf(
            SongWriterSplit(
                releaseId = releaseId,
                contributorName = artistName,
                role = "Primary Artist & Lyricist",
                percentage = 60.0,
                proAffiliation = "BMI",
                ipiNumber = "00847291034"
            ),
            SongWriterSplit(
                releaseId = releaseId,
                contributorName = "Nova Sound Lab",
                role = "Music Producer & Composer",
                percentage = 40.0,
                proAffiliation = "ASCAP",
                ipiNumber = "00918237461"
            )
        )
        splitSheetDao.insertSplits(defaultSplits)

        // Seed default promotional tasks
        val defaultTasks = listOf(
            PromoCampaignTask(
                releaseId = releaseId,
                platform = "TikTok",
                title = "Hook snippet lip-sync / vibe video",
                contentIdea = "Point of view video showing the exact moment the chorus drop hits.",
                scheduledDateMillis = releaseDateMillis - (10L * 24 * 60 * 60 * 1000),
                cost = 0.0
            ),
            PromoCampaignTask(
                releaseId = releaseId,
                platform = "Instagram Reels",
                title = "Studio vocal booth recording BTS",
                contentIdea = "Raw vocal take transitioning into the fully mastered track.",
                scheduledDateMillis = releaseDateMillis - (6L * 24 * 60 * 60 * 1000),
                cost = 0.0
            ),
            PromoCampaignTask(
                releaseId = releaseId,
                platform = "Spotify Pitch",
                title = "Submit Spotify for Artists Editorial pitch",
                contentIdea = "Emphasize high BPM danceability, moody synths, and targeted playlist vibes.",
                scheduledDateMillis = releaseDateMillis - (14L * 24 * 60 * 60 * 1000),
                cost = 0.0
            ),
            PromoCampaignTask(
                releaseId = releaseId,
                platform = "Press Email",
                title = "Pitch 15 Indie Music Curators",
                contentIdea = "Personalized EPK with private SoundCloud streaming link and high-res press photos.",
                scheduledDateMillis = releaseDateMillis - (5L * 24 * 60 * 60 * 1000),
                cost = 50.0
            ),
            PromoCampaignTask(
                releaseId = releaseId,
                platform = "Meta Ads",
                title = "Instagram / Facebook Ad conversion campaign",
                contentIdea = "Target fans of similar artists leading directly to Spotify pre-save page.",
                scheduledDateMillis = releaseDateMillis - (7L * 24 * 60 * 60 * 1000),
                cost = 150.0
            )
        )
        promoTaskDao.insertTasks(defaultTasks)

        // Seed corresponding Song entity
        val song = Song(
            releaseId = releaseId,
            title = title,
            isrcCode = release.isrcCode,
            releaseDateMillis = releaseDateMillis,
            artistName = artistName,
            genre = genre,
            subGenre = subGenre,
            bpm = bpm,
            musicalKey = musicalKey,
            distributor = distributor,
            coverArtPreset = coverArtPreset,
            pitchBlurb = release.pitchBlurb,
            songwriterCreditsJson = serializeSplits(defaultSplits)
        )
        songDao.insertSong(song)

        return releaseId
    }

    suspend fun updateRelease(release: SongRelease) {
        songReleaseDao.updateRelease(release)
        val existingSong = songDao.getSongByReleaseIdDirect(release.id)
        if (existingSong != null) {
            songDao.updateSong(
                existingSong.copy(
                    title = release.title,
                    isrcCode = release.isrcCode,
                    releaseDateMillis = release.releaseDateMillis,
                    artistName = release.artistName,
                    genre = release.genre,
                    subGenre = release.subGenre,
                    bpm = release.bpm,
                    musicalKey = release.musicalKey,
                    explicitLyrics = release.explicitLyrics,
                    upcCode = release.upcCode,
                    distributor = release.distributor,
                    coverArtPreset = release.coverArtPreset,
                    lyrics = release.lyrics,
                    pitchBlurb = release.pitchBlurb,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateEssentialMetadataAndSplits(
        releaseId: Long,
        title: String,
        isrcCode: String,
        releaseDateMillis: Long,
        splits: List<SongWriterSplit>
    ) {
        val release = songReleaseDao.getReleaseByIdDirect(releaseId) ?: return
        val updated = release.copy(
            title = title.trim(),
            isrcCode = isrcCode.trim(),
            releaseDateMillis = releaseDateMillis
        )
        songReleaseDao.updateRelease(updated)
        splitSheetDao.deleteSplitsForRelease(releaseId)
        val formattedSplits = splits.map { it.copy(id = 0, releaseId = releaseId) }
        splitSheetDao.insertSplits(formattedSplits)

        // Persist to Song entity in Room
        val existingSong = songDao.getSongByReleaseIdDirect(releaseId)
        val splitsJson = serializeSplits(splits)
        if (existingSong != null) {
            val updatedSong = existingSong.copy(
                title = title.trim(),
                isrcCode = isrcCode.trim(),
                releaseDateMillis = releaseDateMillis,
                songwriterCreditsJson = splitsJson,
                updatedAt = System.currentTimeMillis()
            )
            songDao.updateSong(updatedSong)
        } else {
            val newSong = Song(
                releaseId = releaseId,
                title = title.trim(),
                isrcCode = isrcCode.trim(),
                releaseDateMillis = releaseDateMillis,
                artistName = release.artistName,
                genre = release.genre,
                subGenre = release.subGenre,
                bpm = release.bpm,
                musicalKey = release.musicalKey,
                distributor = release.distributor,
                coverArtPreset = release.coverArtPreset,
                pitchBlurb = release.pitchBlurb,
                songwriterCreditsJson = splitsJson
            )
            songDao.insertSong(newSong)
        }
    }

    suspend fun updateSplitsForRelease(releaseId: Long, splits: List<SongWriterSplit>) {
        splitSheetDao.deleteSplitsForRelease(releaseId)
        val formattedSplits = splits.map { it.copy(id = 0, releaseId = releaseId) }
        splitSheetDao.insertSplits(formattedSplits)

        // Persist to Song entity in Room
        val existingSong = songDao.getSongByReleaseIdDirect(releaseId)
        val splitsJson = serializeSplits(splits)
        if (existingSong != null) {
            val updatedSong = existingSong.copy(
                songwriterCreditsJson = splitsJson,
                updatedAt = System.currentTimeMillis()
            )
            songDao.updateSong(updatedSong)
        }
    }

    suspend fun deleteRelease(releaseId: Long) {
        checklistDao.deleteChecklistForRelease(releaseId)
        splitSheetDao.deleteSplitsForRelease(releaseId)
        promoTaskDao.deleteTasksForRelease(releaseId)
        songDao.deleteSongsByReleaseId(releaseId)
        songReleaseDao.deleteReleaseById(releaseId)
    }

    private fun serializeSplits(splits: List<SongWriterSplit>): String {
        val builder = StringBuilder("[")
        splits.forEachIndexed { index, s ->
            builder.append("""{"name":"${s.contributorName.replace("\"", "\\\"")}","role":"${s.role.replace("\"", "\\\"")}","percentage":${s.percentage},"pro":"${s.proAffiliation.replace("\"", "\\\"")}","ipi":"${s.ipiNumber.replace("\"", "\\\"")}"}""")
            if (index < splits.size - 1) builder.append(",")
        }
        builder.append("]")
        return builder.toString()
    }

    suspend fun updateChecklistItem(item: ReleaseChecklistItem) = checklistDao.updateChecklistItem(item)
    suspend fun insertChecklistItem(item: ReleaseChecklistItem) = checklistDao.insertChecklistItem(item)
    suspend fun deleteChecklistItem(item: ReleaseChecklistItem) = checklistDao.deleteChecklistItem(item)

    suspend fun insertSplit(split: SongWriterSplit) = splitSheetDao.insertSplit(split)
    suspend fun updateSplit(split: SongWriterSplit) = splitSheetDao.updateSplit(split)
    suspend fun deleteSplit(split: SongWriterSplit) = splitSheetDao.deleteSplit(split)

    suspend fun insertPromoTask(task: PromoCampaignTask) = promoTaskDao.insertTask(task)
    suspend fun updatePromoTask(task: PromoCampaignTask) = promoTaskDao.updateTask(task)
    suspend fun deletePromoTask(task: PromoCampaignTask) = promoTaskDao.deleteTask(task)

    suspend fun ensureInitialData() {
        val existing = songReleaseDao.getAllReleases().first()
        if (existing.isEmpty()) {
            // Seed first flagship song release
            val releaseDate = System.currentTimeMillis() + (12L * 24 * 60 * 60 * 1000) + (14L * 60 * 60 * 1000) // ~12 days out
            createReleaseWithDefaults(
                title = "Midnight Echoes",
                artistName = "Astraea",
                featuredArtists = "feat. Kairo",
                genre = "Electronic / Synthwave",
                subGenre = "Darkwave Pop",
                releaseDateMillis = releaseDate,
                bpm = 126,
                musicalKey = "F# Minor",
                distributor = "DistroKid",
                coverArtPreset = "neon",
                pitchBlurb = "\"Midnight Echoes\" merges analog synth nostalgia with driving 808 basslines and intimate, haunting vocal hooks. Produced by Astraea in collaboration with Kairo, this track is crafted for night drive playlists, synthpop enthusiasts, and high-energy electronic rotations."
            )

            // Seed second acoustic/indie song release
            val acousticReleaseDate = System.currentTimeMillis() + (35L * 24 * 60 * 60 * 1000)
            createReleaseWithDefaults(
                title = "Golden Hour Dreams",
                artistName = "Astraea",
                featuredArtists = "",
                genre = "Indie Folk",
                subGenre = "Acoustic Singer-Songwriter",
                releaseDateMillis = acousticReleaseDate,
                bpm = 92,
                musicalKey = "G Major",
                distributor = "TuneCore",
                coverArtPreset = "acoustic",
                pitchBlurb = "An organic acoustic anthem recorded with fingerpicked vintage mahogany guitar, warm string quartet layers, and reflective lyrics about finding solace in fleeting moments."
            )
        }
    }

    private fun generateSampleIsrc(): String {
        val year = "26"
        val randomNum = (10000..99999).random()
        return "US-AST-$year-$randomNum"
    }

    private fun generateSampleUpc(): String {
        val randomNum = (100000000000L..999999999999L).random()
        return "$randomNum"
    }
}
