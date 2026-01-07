package com.siratalmustaqim.alnoor.ui.screens.settings.prayer

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
class PrayerSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val currentLocation: StateFlow<String> = settingsDataStore.currentLocation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Not set")

    val azanAudio: StateFlow<String> = settingsDataStore.azanAudio
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Default")

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

    companion object {
        val availableAzanAudios = listOf("Default", "Makkah", "Madinah", "Al-Aqsa", "Silent")
    }
}
