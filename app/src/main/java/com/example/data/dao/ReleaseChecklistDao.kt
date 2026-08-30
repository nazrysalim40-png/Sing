package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseChecklistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ReleaseChecklistDao {
    @Query("SELECT * FROM release_checklists WHERE releaseId = :releaseId ORDER BY id ASC")
    fun getChecklistForRelease(releaseId: Long): Flow<List<ReleaseChecklistItem>>

    @Query("SELECT * FROM release_checklists WHERE releaseId = :releaseId AND category = :category ORDER BY id ASC")
    fun getChecklistByCategory(releaseId: Long, category: ChecklistCategory): Flow<List<ReleaseChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ReleaseChecklistItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ReleaseChecklistItem): Long

    @Update
    suspend fun updateChecklistItem(item: ReleaseChecklistItem)

    @Delete
    suspend fun deleteChecklistItem(item: ReleaseChecklistItem)

    @Query("DELETE FROM release_checklists WHERE releaseId = :releaseId")
    suspend fun deleteChecklistForRelease(releaseId: Long)
}
