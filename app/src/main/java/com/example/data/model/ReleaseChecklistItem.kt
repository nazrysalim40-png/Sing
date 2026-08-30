package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ChecklistCategory(val displayName: String, val iconName: String) {
    AUDIO_MASTER("Audio & Masters", "graphic_eq"),
    ARTWORK_ASSETS("Visuals & Artwork", "image"),
    METADATA_LEGAL("Metadata & Splits", "verified_user"),
    DISTRIBUTION("Distributor & Pitching", "send"),
    MARKETING_PROMO("Marketing & Teasers", "campaign"),
    DROP_DAY("Release Day Drop", "celebration")
}

@Entity(tableName = "release_checklists")
data class ReleaseChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val releaseId: Long,
    val category: ChecklistCategory,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val milestoneDaysOffset: Int = -14, // e.g. -28 = 4w before, -7 = 1w before, 0 = drop day
    val isRequired: Boolean = true
)
