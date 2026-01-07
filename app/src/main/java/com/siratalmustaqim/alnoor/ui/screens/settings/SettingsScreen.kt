package com.siratalmustaqim.alnoor.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.ui.screens.settings.guard.GuardSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.prayer.PrayerSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.quran.QuranSettingsSection
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreenContent(
        uiState = uiState,
        onAyahTextSizeChange = viewModel::updateAyahTextSize,
        onAyahFontChange = viewModel::updateAyahFont,
        onLocationClick = { /* TODO: Open location picker */ },
        onAzanAudioChange = viewModel::updateAzanAudio,
        onVpnToggle = viewModel::toggleVpn
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onAyahTextSizeChange: (Float) -> Unit,
    onAyahFontChange: (String) -> Unit,
    onLocationClick: () -> Unit,
    onAzanAudioChange: (String) -> Unit,
    onVpnToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Quran Settings Section
        QuranSettingsSection(
            uiState = uiState.quran,
            onTextSizeChange = onAyahTextSizeChange,
            onFontChange = onAyahFontChange
        )

        // Prayer Settings Section
        PrayerSettingsSection(
            uiState = uiState.prayer,
            onLocationClick = onLocationClick,
            onAzanAudioChange = onAzanAudioChange
        )

        // Guard Settings Section
        GuardSettingsSection(
            uiState = uiState.guard,
            onVpnToggle = onVpnToggle
        )

        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AlNoorTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(),
            onAyahTextSizeChange = {},
            onAyahFontChange = {},
            onLocationClick = {},
            onAzanAudioChange = {},
            onVpnToggle = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        SettingsScreenContent(
            uiState = SettingsUiState(
                quran = QuranSettingsUiState(ayahTextSize = 24f, ayahFont = "Scheherazade"),
                prayer = PrayerSettingsUiState(currentLocation = "New York, USA", azanAudio = "Makkah"),
                guard = GuardSettingsUiState(vpnEnabled = true)
            ),
            onAyahTextSizeChange = {},
            onAyahFontChange = {},
            onLocationClick = {},
            onAzanAudioChange = {},
            onVpnToggle = {}
        )
    }
}
