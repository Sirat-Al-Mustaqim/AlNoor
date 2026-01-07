package com.siratalmustaqim.alnoor.ui.screens.settings.prayer

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

@HiltViewModel
class PrayerSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<PrayerSettingsUiState> = combine(
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

    private fun onLocationClick() {
        // TODO: Open location picker
    }

    private fun updateAzanAudio(audio: String) {
        viewModelScope.launch {
            settingsDataStore.updateAzanAudio(audio)
        }
    }
}
