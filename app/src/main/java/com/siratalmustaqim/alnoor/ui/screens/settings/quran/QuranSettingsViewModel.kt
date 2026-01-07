package com.siratalmustaqim.alnoor.ui.screens.settings.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuranSettingsUiState(
    val ayahTextSize: Float = 20f,
    val ayahFont: String = "Amiri",
    val onTextSizeChange: (Float) -> Unit = {},
    val onFontChange: (String) -> Unit = {}
) {
    companion object {
        val availableFonts = listOf("Amiri", "Scheherazade", "Noto Naskh Arabic")
    }
}

@HiltViewModel
class QuranSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<QuranSettingsUiState> = combine(
        settingsDataStore.ayahTextSize,
        settingsDataStore.ayahFont
    ) { textSize, font ->
        QuranSettingsUiState(
            ayahTextSize = textSize,
            ayahFont = font,
            onTextSizeChange = ::updateTextSize,
            onFontChange = ::updateFont
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuranSettingsUiState(
            onTextSizeChange = ::updateTextSize,
            onFontChange = ::updateFont
        )
    )

    private fun updateTextSize(size: Float) {
        viewModelScope.launch {
            settingsDataStore.updateAyahTextSize(size)
        }
    }

    private fun updateFont(font: String) {
        viewModelScope.launch {
            settingsDataStore.updateAyahFont(font)
        }
    }
}
