package com.siratalmustaqim.alnoor.ui.screens.settings.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val ayahTextSize: StateFlow<Float> = settingsDataStore.ayahTextSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20f)

    val ayahFont: StateFlow<String> = settingsDataStore.ayahFont
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Amiri")

    fun updateTextSize(size: Float) {
        viewModelScope.launch {
            settingsDataStore.updateAyahTextSize(size)
        }
    }

    fun updateFont(font: String) {
        viewModelScope.launch {
            settingsDataStore.updateAyahFont(font)
        }
    }

    companion object {
        val availableFonts = listOf("Amiri", "Scheherazade", "Noto Naskh Arabic")
    }
}
