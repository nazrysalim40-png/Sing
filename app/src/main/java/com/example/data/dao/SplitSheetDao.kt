package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SongWriterSplit
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitSheetDao {
    @Query("SELECT * FROM songwriter_splits WHERE releaseId = :releaseId ORDER BY percentage DESC")
    fun getSplitsForRelease(releaseId: Long): Flow<List<SongWriterSplit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplits(splits: List<SongWriterSplit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplit(split: SongWriterSplit): Long

    @Update
    suspend fun updateSplit(split: SongWriterSplit)

    @Delete
    suspend fun deleteSplit(split: SongWriterSplit)

    @Query("DELETE FROM songwriter_splits WHERE releaseId = :releaseId")
    suspend fun deleteSplitsForRelease(releaseId: Long)
}
