package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseStatus

class Converters {
    @TypeConverter
    fun fromReleaseStatus(status: ReleaseStatus?): String {
        return status?.name ?: ReleaseStatus.SCHEDULED.name
    }

    @TypeConverter
    fun toReleaseStatus(value: String?): ReleaseStatus {
        return try {
            value?.let { ReleaseStatus.valueOf(it) } ?: ReleaseStatus.SCHEDULED
        } catch (e: Exception) {
            ReleaseStatus.SCHEDULED
        }
    }

    @TypeConverter
    fun fromChecklistCategory(category: ChecklistCategory?): String {
        return category?.name ?: ChecklistCategory.AUDIO_MASTER.name
    }

    @TypeConverter
    fun toChecklistCategory(value: String?): ChecklistCategory {
        return try {
            value?.let { ChecklistCategory.valueOf(it) } ?: ChecklistCategory.AUDIO_MASTER
        } catch (e: Exception) {
            ChecklistCategory.AUDIO_MASTER
        }
    }
}
