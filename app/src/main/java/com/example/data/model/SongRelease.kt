package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReleaseStatus(val displayName: String, val stepIndex: Int) {
    CONCEPT("Concept & Demo", 0),
    IN_PRODUCTION("In Production", 1),
    MIX_MASTER("Mixing & Mastering", 2),
    SCHEDULED("Scheduled with Distributor", 3),
    PRE_SAVE_ACTIVE("Pre-Save Live", 4),
    DROPPED("Released & Live", 5)
}

@Entity(tableName = "song_releases")
data class SongRelease(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artistName: String,
    val featuredArtists: String = "",
    val genre: String = "Pop",
    val subGenre: String = "Synthpop",
    val releaseDateMillis: Long = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000), // Default 2 weeks
    val status: ReleaseStatus = ReleaseStatus.SCHEDULED,
    val isrcCode: String = "",
    val upcCode: String = "",
    val bpm: Int = 120,
    val musicalKey: String = "C Major",
    val explicitLyrics: Boolean = false,
    val distributor: String = "DistroKid",
    val distributorSubmitted: Boolean = false,
    val spotifyEditorialPitched: Boolean = false,
    val preSaveUrl: String = "",
    val dspSpotifyUrl: String = "",
    val dspAppleMusicUrl: String = "",
    val coverArtPreset: String = "neon", // "neon", "acoustic", "studio"
    val lyrics: String = "",
    val pitchBlurb: String = "",
    val targetBudget: Double = 500.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns the selected genres as a cleaned list.
     */
    val genreList: List<String>
        get() = if (genre.isBlank()) emptyList() else genre.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
