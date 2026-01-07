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
    val currentCity: String = "Unknown",
    val currentCountry: String = "",
    val autoDetectLocation: Boolean = true,
    val adhanVolume: Float = 0.85f,
    val azanAudio: String = "Mishary Rashid Alafasy",
    val silentDuringPrayer: Boolean = false,
    val earlyReminder: Boolean = true,
    val onLocationClick: () -> Unit = {},
    val onAutoDetectToggle: (Boolean) -> Unit = {},
    val onAdhanVolumeChange: (Float) -> Unit = {},
    val onAzanAudioChange: (String) -> Unit = {},
    val onSilentDuringPrayerToggle: (Boolean) -> Unit = {},
    val onEarlyReminderToggle: (Boolean) -> Unit = {}
) {
    companion object {
        val availableReciters = listOf(
            "Mishary Rashid Alafasy",
            "Abdul Basit",
            "Masjid Al-Haram",
            "Masjid An-Nabawi",
            "Silent"
        )
    }
}

@HiltViewModel
class PrayerSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Combine flows in pairs to avoid type inference issues
    private val locationAndAutoDetect = combine(
        settingsDataStore.currentLocation,
        settingsDataStore.autoDetectLocation
    ) { location, autoDetect -> location to autoDetect }

    private val volumeAndAzan = combine(
        settingsDataStore.adhanVolume,
        settingsDataStore.azanAudio
    ) { volume, azan -> volume to azan }

    private val silentAndEarly = combine(
        settingsDataStore.silentDuringPrayer,
        settingsDataStore.earlyReminder
    ) { silent, early -> silent to early }

    val uiState: StateFlow<PrayerSettingsUiState> = combine(
        locationAndAutoDetect,
        volumeAndAzan,
        silentAndEarly
    ) { (location, autoDetect), (volume, azan), (silent, early) ->
        // Parse location into city and country
        val parts = location.split(", ")
        val city = parts.getOrNull(0) ?: "Unknown"
        val country = parts.getOrNull(1) ?: ""

        PrayerSettingsUiState(
            currentLocation = location,
            currentCity = city,
            currentCountry = country,
            autoDetectLocation = autoDetect,
            adhanVolume = volume,
            azanAudio = azan,
            silentDuringPrayer = silent,
            earlyReminder = early,
            onLocationClick = ::onLocationClick,
            onAutoDetectToggle = ::updateAutoDetect,
            onAdhanVolumeChange = ::updateVolume,
            onAzanAudioChange = ::updateAzanAudio,
            onSilentDuringPrayerToggle = ::updateSilentDuringPrayer,
            onEarlyReminderToggle = ::updateEarlyReminder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrayerSettingsUiState(
            onLocationClick = ::onLocationClick,
            onAutoDetectToggle = ::updateAutoDetect,
            onAdhanVolumeChange = ::updateVolume,
            onAzanAudioChange = ::updateAzanAudio,
            onSilentDuringPrayerToggle = ::updateSilentDuringPrayer,
            onEarlyReminderToggle = ::updateEarlyReminder
        )
    )

    private fun onLocationClick() {
        // TODO: Open location picker
    }

    private fun updateAutoDetect(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateAutoDetectLocation(enabled)
        }
    }

    private fun updateVolume(volume: Float) {
        viewModelScope.launch {
            settingsDataStore.updateAdhanVolume(volume)
        }
    }

    private fun updateAzanAudio(audio: String) {
        viewModelScope.launch {
            settingsDataStore.updateAzanAudio(audio)
        }
    }

    private fun updateSilentDuringPrayer(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSilentDuringPrayer(enabled)
        }
    }

    private fun updateEarlyReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateEarlyReminder(enabled)
        }
    }
}
