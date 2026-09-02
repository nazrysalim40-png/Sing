package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a Song and its persistent metadata collected
 * from the Song Release & Track Metadata forms.
 */
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isrcCode: String = "",
    val releaseDateMillis: Long = System.currentTimeMillis(),
    val artistName: String = "",
    val genre: String = "Pop",
    val subGenre: String = "Synthpop",
    val bpm: Int = 120,
    val musicalKey: String = "C Major",
    val explicitLyrics: Boolean = false,
    val upcCode: String = "",
    val distributor: String = "DistroKid",
    val coverArtPreset: String = "neon",
    val lyrics: String = "",
    val pitchBlurb: String = "",
    val songwriterCreditsJson: String = "[]",
    val releaseId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns the selected genres as a cleaned list.
     */
    val genreList: List<String>
        get() = if (genre.isBlank()) emptyList() else genre.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
