package com.siratalmustaqim.alnoor.ui.screens.settings

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

// UI State data classes with embedded update functions
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

data class PrayerSettingsUiState(
    val currentLocation: String = "Not set",
    val azanAudio: String = "Default",
    val onLocationClick: () -> Unit = {},
    val onAzanAudioChange: (String) -> Unit = {}
) {
    companion object {
        val availableAzanAudios = listOf("Default", "Makkah", "Madinah", "Al-Aqsa", "Silent")
    }
}

data class GuardSettingsUiState(
    val vpnEnabled: Boolean = false,
    val onVpnToggle: (Boolean) -> Unit = {}
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val quranUiState: StateFlow<QuranSettingsUiState> = combine(
        settingsDataStore.ayahTextSize,
        settingsDataStore.ayahFont
    ) { textSize, font ->
        QuranSettingsUiState(
            ayahTextSize = textSize,
            ayahFont = font,
            onTextSizeChange = ::updateAyahTextSize,
            onFontChange = ::updateAyahFont
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuranSettingsUiState(
            onTextSizeChange = ::updateAyahTextSize,
            onFontChange = ::updateAyahFont
        )
    )

    val prayerUiState: StateFlow<PrayerSettingsUiState> = combine(
        settingsDataStore.currentLocation,
        settingsDataStore.azanAudio
    ) { location, azanAudio ->
        PrayerSettingsUiState(
            currentLocation = location,
            azanAudio = azanAudio,
            onLocationClick = ::onLocationClick,
            onAzanAudioChange = ::updateAzanAudio
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrayerSettingsUiState(
            onLocationClick = ::onLocationClick,
            onAzanAudioChange = ::updateAzanAudio
        )
    )

    val guardUiState: StateFlow<GuardSettingsUiState> = settingsDataStore.vpnEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        ).let { vpnFlow ->
            combine(vpnFlow) { values ->
                GuardSettingsUiState(
                    vpnEnabled = values[0],
                    onVpnToggle = ::toggleVpn
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GuardSettingsUiState(onVpnToggle = ::toggleVpn)
            )
        }

    // Quran Settings Actions
    private fun updateAyahTextSize(size: Float) {
        viewModelScope.launch {
            settingsDataStore.updateAyahTextSize(size)
        }
    }

    private fun updateAyahFont(font: String) {
        viewModelScope.launch {
            settingsDataStore.updateAyahFont(font)
        }
    }

    // Prayer Settings Actions
    private fun onLocationClick() {
        // TODO: Open location picker
    }

    private fun updateAzanAudio(audio: String) {
        viewModelScope.launch {
            settingsDataStore.updateAzanAudio(audio)
        }
    }

    // Guard Settings Actions
    private fun toggleVpn(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateVpnEnabled(enabled)
        }
    }
}
