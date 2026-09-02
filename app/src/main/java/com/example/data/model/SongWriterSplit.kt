package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songwriter_splits")
data class SongWriterSplit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val releaseId: Long = 0,
    val contributorName: String,
    val role: String, // "Primary Artist", "Producer", "Lyricist", "Composer", "Mixing Engineer"
    val percentage: Double, // e.g. 50.0
    val proAffiliation: String = "ASCAP", // "ASCAP", "BMI", "PRS", "SOCAN", "None"
    val ipiNumber: String = "",
    val publisher: String = "",
    val email: String = "",
    val phone: String = ""
) {
    val formattedPercentage: String
        get() = "%.1f".format(percentage)

    val hasContactDetails: Boolean
        get() = email.isNotBlank() || phone.isNotBlank()
}
