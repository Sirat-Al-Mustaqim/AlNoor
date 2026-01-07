package com.siratalmustaqim.alnoor.ui.screens.settings.prayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowText
import com.siratalmustaqim.alnoor.ui.components.SettingsSection

@Composable
fun PrayerSettingsSection(
    modifier: Modifier = Modifier,
    viewModel: PrayerSettingsViewModel = hiltViewModel()
) {
    val location by viewModel.currentLocation.collectAsState()
    val azanAudio by viewModel.azanAudio.collectAsState()

    SettingsSection(
        title = "Prayer",
        icon = "🕌",
        modifier = modifier
    ) {
        SettingsRowText(
            label = "Current Location",
            value = location,
            onClick = {
                // TODO: Open location picker dialog
            }
        )

        SettingsRowDropdown(
            label = "Azan Audio",
            selectedValue = azanAudio,
            options = PrayerSettingsViewModel.availableAzanAudios,
            onOptionSelected = { viewModel.updateAzanAudio(it) }
        )
    }
}
