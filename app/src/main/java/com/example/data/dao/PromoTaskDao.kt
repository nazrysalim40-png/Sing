package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PromoCampaignTask
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoTaskDao {
    @Query("SELECT * FROM promo_campaign_tasks WHERE releaseId = :releaseId ORDER BY scheduledDateMillis ASC")
    fun getTasksForRelease(releaseId: Long): Flow<List<PromoCampaignTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<PromoCampaignTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PromoCampaignTask): Long

    @Update
    suspend fun updateTask(task: PromoCampaignTask)

    @Delete
    suspend fun deleteTask(task: PromoCampaignTask)

    @Query("DELETE FROM promo_campaign_tasks WHERE releaseId = :releaseId")
    suspend fun deleteTasksForRelease(releaseId: Long)
}
