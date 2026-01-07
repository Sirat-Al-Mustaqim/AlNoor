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
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreenContent(
        quranUiStateFlow = viewModel.quranUiState,
        prayerUiStateFlow = viewModel.prayerUiState,
        guardUiStateFlow = viewModel.guardUiState
    )
}

@Composable
private fun SettingsScreenContent(
    quranUiStateFlow: StateFlow<QuranSettingsUiState>,
    prayerUiStateFlow: StateFlow<PrayerSettingsUiState>,
    guardUiStateFlow: StateFlow<GuardSettingsUiState>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header - this won't recompose on state changes
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Each section collects its own state - isolated recomposition!
        QuranSettingsSectionWrapper(uiStateFlow = quranUiStateFlow)
        PrayerSettingsSectionWrapper(uiStateFlow = prayerUiStateFlow)
        GuardSettingsSectionWrapper(uiStateFlow = guardUiStateFlow)

        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun QuranSettingsSectionWrapper(uiStateFlow: StateFlow<QuranSettingsUiState>) {
    val uiState by uiStateFlow.collectAsState()
    QuranSettingsSection(uiState = uiState)
}

@Composable
private fun PrayerSettingsSectionWrapper(uiStateFlow: StateFlow<PrayerSettingsUiState>) {
    val uiState by uiStateFlow.collectAsState()
    PrayerSettingsSection(uiState = uiState)
}

@Composable
private fun GuardSettingsSectionWrapper(uiStateFlow: StateFlow<GuardSettingsUiState>) {
    val uiState by uiStateFlow.collectAsState()
    GuardSettingsSection(uiState = uiState)
}

// Previews use direct UiState (not StateFlow)
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AlNoorTheme {
        SettingsScreenPreviewContent()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        SettingsScreenPreviewContent(
            quranUiState = QuranSettingsUiState(ayahTextSize = 24f, ayahFont = "Scheherazade"),
            prayerUiState = PrayerSettingsUiState(currentLocation = "New York, USA", azanAudio = "Makkah"),
            guardUiState = GuardSettingsUiState(vpnEnabled = true)
        )
    }
}

@Composable
private fun SettingsScreenPreviewContent(
    quranUiState: QuranSettingsUiState = QuranSettingsUiState(),
    prayerUiState: PrayerSettingsUiState = PrayerSettingsUiState(),
    guardUiState: GuardSettingsUiState = GuardSettingsUiState()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        QuranSettingsSection(uiState = quranUiState)
        PrayerSettingsSection(uiState = prayerUiState)
        GuardSettingsSection(uiState = guardUiState)
        Spacer(modifier = Modifier.height(80.dp))
    }
}
