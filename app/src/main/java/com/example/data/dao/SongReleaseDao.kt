package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SongRelease
import kotlinx.coroutines.flow.Flow

@Dao
interface SongReleaseDao {
    @Query("SELECT * FROM song_releases ORDER BY releaseDateMillis ASC")
    fun getAllReleases(): Flow<List<SongRelease>>

    @Query("SELECT * FROM song_releases WHERE id = :id")
    fun getReleaseById(id: Long): Flow<SongRelease?>

    @Query("SELECT * FROM song_releases WHERE id = :id")
    suspend fun getReleaseByIdDirect(id: Long): SongRelease?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelease(release: SongRelease): Long

    @Update
    suspend fun updateRelease(release: SongRelease)

    @Delete
    suspend fun deleteRelease(release: SongRelease)

    @Query("DELETE FROM song_releases WHERE id = :id")
    suspend fun deleteReleaseById(id: Long)
}
