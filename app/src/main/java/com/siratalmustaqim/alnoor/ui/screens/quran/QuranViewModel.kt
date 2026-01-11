package com.siratalmustaqim.alnoor.ui.screens.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.local.entity.Ayah
import com.siratalmustaqim.alnoor.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Quran screen.
 */
data class QuranUiState(
    val isLoading: Boolean = true,
    val selectedSurah: Int = 1,
    val ayahs: List<Ayah> = emptyList(),
    val surahNumbers: List<Int> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for the Quran screen.
 */
@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        loadSurahList()
        loadSurah(1) // Load Al-Fatiha by default
    }

    /**
     * Load the list of all surah numbers.
     */
    private fun loadSurahList() {
        viewModelScope.launch {
            try {
                val surahNumbers = quranRepository.getAllSurahNumbers()
                _uiState.value = _uiState.value.copy(surahNumbers = surahNumbers)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load surah list: ${e.message}"
                )
            }
        }
    }

    /**
     * Load ayahs for a specific surah.
     */
    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedSurah = surahNumber
            )
            try {
                val ayahs = quranRepository.getAyahsBySurahOnce(surahNumber)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    ayahs = ayahs,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load surah: ${e.message}"
                )
            }
        }
    }

    /**
     * Navigate to the next surah.
     */
    fun nextSurah() {
        val currentSurah = _uiState.value.selectedSurah
        if (currentSurah < 114) {
            loadSurah(currentSurah + 1)
        }
    }

    /**
     * Navigate to the previous surah.
     */
    fun previousSurah() {
        val currentSurah = _uiState.value.selectedSurah
        if (currentSurah > 1) {
            loadSurah(currentSurah - 1)
        }
    }
}
