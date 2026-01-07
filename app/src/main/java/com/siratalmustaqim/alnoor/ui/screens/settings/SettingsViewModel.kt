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

// UI State data classes for each section
data class QuranSettingsUiState(
    val ayahTextSize: Float = 20f,
    val ayahFont: String = "Amiri"
) {
    companion object {
        val availableFonts = listOf("Amiri", "Scheherazade", "Noto Naskh Arabic")
    }
}

data class PrayerSettingsUiState(
    val currentLocation: String = "Not set",
    val azanAudio: String = "Default"
) {
    companion object {
        val availableAzanAudios = listOf("Default", "Makkah", "Madinah", "Al-Aqsa", "Silent")
    }
}

data class GuardSettingsUiState(
    val vpnEnabled: Boolean = false
)

data class SettingsUiState(
    val quran: QuranSettingsUiState = QuranSettingsUiState(),
    val prayer: PrayerSettingsUiState = PrayerSettingsUiState(),
    val guard: GuardSettingsUiState = GuardSettingsUiState()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.ayahTextSize,
        settingsDataStore.ayahFont,
        settingsDataStore.currentLocation,
        settingsDataStore.azanAudio,
        settingsDataStore.vpnEnabled
    ) { textSize, font, location, azanAudio, vpnEnabled ->
        SettingsUiState(
            quran = QuranSettingsUiState(
                ayahTextSize = textSize,
                ayahFont = font
            ),
            prayer = PrayerSettingsUiState(
                currentLocation = location,
                azanAudio = azanAudio
            ),
            guard = GuardSettingsUiState(
                vpnEnabled = vpnEnabled
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    // Quran Settings Actions
    fun updateAyahTextSize(size: Float) {
        viewModelScope.launch {
            settingsDataStore.updateAyahTextSize(size)
        }
    }

    fun updateAyahFont(font: String) {
        viewModelScope.launch {
            settingsDataStore.updateAyahFont(font)
        }
    }

    // Prayer Settings Actions
    fun updateLocation(location: String) {
        viewModelScope.launch {
            settingsDataStore.updateCurrentLocation(location)
        }
    }

    fun updateAzanAudio(audio: String) {
        viewModelScope.launch {
            settingsDataStore.updateAzanAudio(audio)
        }
    }

    // Guard Settings Actions
    fun toggleVpn(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateVpnEnabled(enabled)
        }
    }
}
