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
    val quranUiState by viewModel.quranUiState.collectAsState()
    val prayerUiState by viewModel.prayerUiState.collectAsState()
    val guardUiState by viewModel.guardUiState.collectAsState()

    SettingsScreenContent(
        quranUiState = quranUiState,
        prayerUiState = prayerUiState,
        guardUiState = guardUiState
    )
}

@Composable
private fun SettingsScreenContent(
    quranUiState: QuranSettingsUiState,
    prayerUiState: PrayerSettingsUiState,
    guardUiState: GuardSettingsUiState
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
        QuranSettingsSection(uiState = quranUiState)

        // Prayer Settings Section
        PrayerSettingsSection(uiState = prayerUiState)

        // Guard Settings Section
        GuardSettingsSection(uiState = guardUiState)

        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AlNoorTheme {
        SettingsScreenContent(
            quranUiState = QuranSettingsUiState(),
            prayerUiState = PrayerSettingsUiState(),
            guardUiState = GuardSettingsUiState()
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        SettingsScreenContent(
            quranUiState = QuranSettingsUiState(ayahTextSize = 24f, ayahFont = "Scheherazade"),
            prayerUiState = PrayerSettingsUiState(currentLocation = "New York, USA", azanAudio = "Makkah"),
            guardUiState = GuardSettingsUiState(vpnEnabled = true)
        )
    }
}
