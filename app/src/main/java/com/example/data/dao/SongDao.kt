package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Song entity in Room.
 */
@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY releaseDateMillis DESC, id DESC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    fun getSongById(id: Long): Flow<Song?>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongByIdDirect(id: Long): Song?

    @Query("SELECT * FROM songs WHERE releaseId = :releaseId LIMIT 1")
    fun getSongByReleaseId(releaseId: Long): Flow<Song?>

    @Query("SELECT * FROM songs WHERE releaseId = :releaseId LIMIT 1")
    suspend fun getSongByReleaseIdDirect(releaseId: Long): Song?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>): List<Long>

    @Update
    suspend fun updateSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSongById(id: Long)

    @Query("DELETE FROM songs WHERE releaseId = :releaseId")
    suspend fun deleteSongsByReleaseId(releaseId: Long)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}
