package com.siratalmustaqim.alnoor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a single Ayah (verse) of the Quran.
 *
 * This maps to the 'ayahs' table in the pre-populated SQLite database.
 */
@Entity(tableName = "ayahs")
data class Ayah(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 1, // Using 1 for auto-generation

    @ColumnInfo(name = "surah_number", defaultValue = "1")
    val surahNumber: Int,

    @ColumnInfo(name = "ayah_number", defaultValue = "1")
    val ayahNumber: Int,

    @ColumnInfo(name = "text")
    val text: String
)
