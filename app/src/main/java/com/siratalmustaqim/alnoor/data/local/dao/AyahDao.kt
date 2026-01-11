package com.siratalmustaqim.alnoor.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.siratalmustaqim.alnoor.data.local.entity.Ayah
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Ayah entities.
 * 
 * Provides methods to query the Quran database for verses.
 */
@Dao
interface AyahDao {
    
    /**
     * Get all ayahs from the database.
     */
    @Query("SELECT * FROM ayahs ORDER BY id ASC")
    fun getAllAyahs(): Flow<List<Ayah>>
    
    /**
     * Get all ayahs for a specific surah.
     * 
     * @param surahNumber The surah number (1-114)
     */
    @Query("SELECT * FROM ayahs WHERE surah_number = :surahNumber ORDER BY ayah_number ASC")
    fun getAyahsBySurah(surahNumber: Int): Flow<List<Ayah>>
    
    /**
     * Get all ayahs for a specific surah (non-Flow version for one-time queries).
     * 
     * @param surahNumber The surah number (1-114)
     */
    @Query("SELECT * FROM ayahs WHERE surah_number = :surahNumber ORDER BY ayah_number ASC")
    suspend fun getAyahsBySurahOnce(surahNumber: Int): List<Ayah>
    
    /**
     * Get a specific ayah by surah and ayah number.
     * 
     * @param surahNumber The surah number (1-114)
     * @param ayahNumber The ayah number within the surah
     */
    @Query("SELECT * FROM ayahs WHERE surah_number = :surahNumber AND ayah_number = :ayahNumber LIMIT 1")
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah?
    
    /**
     * Get an ayah by its global ID.
     * 
     * @param id The unique ID of the ayah (1-6236)
     */
    @Query("SELECT * FROM ayahs WHERE id = :id LIMIT 1")
    suspend fun getAyahById(id: Int): Ayah?
    
    /**
     * Get the total number of ayahs in a surah.
     * 
     * @param surahNumber The surah number (1-114)
     */
    @Query("SELECT COUNT(*) FROM ayahs WHERE surah_number = :surahNumber")
    suspend fun getAyahCountInSurah(surahNumber: Int): Int
    
    /**
     * Get the total number of ayahs in the entire Quran.
     */
    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getTotalAyahCount(): Int
    
    /**
     * Search for ayahs containing the given text.
     * 
     * @param searchQuery The text to search for
     */
    @Query("SELECT * FROM ayahs WHERE text LIKE '%' || :searchQuery || '%' ORDER BY id ASC")
    fun searchAyahs(searchQuery: String): Flow<List<Ayah>>
    
    /**
     * Get a range of ayahs by their global IDs.
     * 
     * @param startId The starting ID (inclusive)
     * @param endId The ending ID (inclusive)
     */
    @Query("SELECT * FROM ayahs WHERE id BETWEEN :startId AND :endId ORDER BY id ASC")
    fun getAyahsInRange(startId: Int, endId: Int): Flow<List<Ayah>>
    
    /**
     * Get the list of distinct surah numbers (useful for listing all surahs).
     */
    @Query("SELECT DISTINCT surah_number FROM ayahs ORDER BY surah_number ASC")
    suspend fun getAllSurahNumbers(): List<Int>
    
    /**
     * Check if an exact ayah text exists in the database.
     * 
     * @param ayahText The exact ayah text to search for
     * @return true if the ayah exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM ayahs WHERE text = :ayahText)")
    suspend fun ayahExists(ayahText: String): Boolean
}
