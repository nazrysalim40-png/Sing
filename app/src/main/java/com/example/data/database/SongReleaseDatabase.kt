package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.PromoTaskDao
import com.example.data.dao.ReleaseChecklistDao
import com.example.data.dao.SongDao
import com.example.data.dao.SongReleaseDao
import com.example.data.dao.SplitSheetDao
import com.example.data.model.PromoCampaignTask
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit

@Database(
    entities = [
        Song::class,
        SongRelease::class,
        ReleaseChecklistItem::class,
        SongWriterSplit::class,
        PromoCampaignTask::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SongReleaseDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun songReleaseDao(): SongReleaseDao
    abstract fun releaseChecklistDao(): ReleaseChecklistDao
    abstract fun splitSheetDao(): SplitSheetDao
    abstract fun promoTaskDao(): PromoTaskDao

    companion object {
        @Volatile
        private var INSTANCE: SongReleaseDatabase? = null

        fun getDatabase(context: Context): SongReleaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SongReleaseDatabase::class.java,
                    "song_release_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
