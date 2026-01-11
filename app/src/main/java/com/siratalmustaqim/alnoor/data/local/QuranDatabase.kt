package com.siratalmustaqim.alnoor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siratalmustaqim.alnoor.data.local.dao.AyahDao
import com.siratalmustaqim.alnoor.data.local.entity.Ayah

/**
 * Room database for Quran data.
 *
 * This database is pre-populated from assets/quran.db which contains
 * the complete Quran text with 6236 verses across 114 surahs.
 *
 * The database is read-only as the Quran text should not be modified.
 */
@Database(
    entities = [Ayah::class],
    version = 1,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {

    abstract fun ayahDao(): AyahDao

    companion object {
        const val DATABASE_NAME = "quran.db"
    }
}
