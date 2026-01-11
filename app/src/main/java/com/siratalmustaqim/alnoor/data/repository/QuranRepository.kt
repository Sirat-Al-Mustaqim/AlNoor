package com.siratalmustaqim.alnoor.data.repository

import com.siratalmustaqim.alnoor.data.local.dao.AyahDao
import com.siratalmustaqim.alnoor.data.local.entity.Ayah
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing Quran data.
 * 
 * Provides a clean API for accessing Quran verses from the pre-populated database.
 */
@Singleton
class QuranRepository @Inject constructor(
    private val ayahDao: AyahDao
) {
    
    /**
     * Get all ayahs from the Quran.
     */
    fun getAllAyahs(): Flow<List<Ayah>> = ayahDao.getAllAyahs()
    
    /**
     * Get all ayahs for a specific surah.
     * 
     * @param surahNumber The surah number (1-114)
     */
    fun getAyahsBySurah(surahNumber: Int): Flow<List<Ayah>> = 
        ayahDao.getAyahsBySurah(surahNumber)
    
    /**
     * Get all ayahs for a specific surah (one-time query).
     * 
     * @param surahNumber The surah number (1-114)
     */
    suspend fun getAyahsBySurahOnce(surahNumber: Int): List<Ayah> = 
        ayahDao.getAyahsBySurahOnce(surahNumber)
    
    /**
     * Get a specific ayah by surah and ayah number.
     * 
     * @param surahNumber The surah number (1-114)
     * @param ayahNumber The ayah number within the surah
     */
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? = 
        ayahDao.getAyah(surahNumber, ayahNumber)
    
    /**
     * Get an ayah by its global ID.
     * 
     * @param id The unique ID of the ayah (1-6236)
     */
    suspend fun getAyahById(id: Int): Ayah? = ayahDao.getAyahById(id)
    
    /**
     * Get the total number of ayahs in a surah.
     * 
     * @param surahNumber The surah number (1-114)
     */
    suspend fun getAyahCountInSurah(surahNumber: Int): Int = 
        ayahDao.getAyahCountInSurah(surahNumber)
    
    /**
     * Get the total number of ayahs in the Quran.
     */
    suspend fun getTotalAyahCount(): Int = ayahDao.getTotalAyahCount()
    
    /**
     * Search for ayahs containing the given text.
     * 
     * @param searchQuery The text to search for
     */
    fun searchAyahs(searchQuery: String): Flow<List<Ayah>> = 
        ayahDao.searchAyahs(searchQuery)
    
    /**
     * Get a range of ayahs by their global IDs.
     * 
     * @param startId The starting ID (inclusive)
     * @param endId The ending ID (inclusive)
     */
    fun getAyahsInRange(startId: Int, endId: Int): Flow<List<Ayah>> = 
        ayahDao.getAyahsInRange(startId, endId)
    
    /**
     * Get all surah numbers.
     */
    suspend fun getAllSurahNumbers(): List<Int> = ayahDao.getAllSurahNumbers()
    
    /**
     * Check if an exact ayah text exists in the database.
     * 
     * @param ayahText The exact ayah text to search for
     * @return true if the ayah exists, false otherwise
     */
    suspend fun searchAyahExact(ayahText: String): Boolean = 
        ayahDao.ayahExists(ayahText)
}
