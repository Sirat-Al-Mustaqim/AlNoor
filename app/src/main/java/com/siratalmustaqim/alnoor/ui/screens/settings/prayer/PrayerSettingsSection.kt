package com.siratalmustaqim.alnoor.ui.screens.settings.prayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowText
import com.siratalmustaqim.alnoor.ui.components.SettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.PrayerSettingsUiState

@Composable
fun PrayerSettingsSection(
    uiState: PrayerSettingsUiState,
    modifier: Modifier = Modifier
) {
    SettingsSection(
        title = "Prayer",
        icon = "🕌",
        modifier = modifier
    ) {
        SettingsRowText(
            label = "Current Location",
            value = uiState.currentLocation,
            onClick = uiState.onLocationClick
        )

        SettingsRowDropdown(
            label = "Azan Audio",
            selectedValue = uiState.azanAudio,
            options = PrayerSettingsUiState.availableAzanAudios,
            onOptionSelected = uiState.onAzanAudioChange
        )
    }
}
