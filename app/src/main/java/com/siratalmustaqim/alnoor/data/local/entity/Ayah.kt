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
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    
    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,
    
    @ColumnInfo(name = "ayah_number")
    val ayahNumber: Int,
    
    @ColumnInfo(name = "text")
    val text: String
)
