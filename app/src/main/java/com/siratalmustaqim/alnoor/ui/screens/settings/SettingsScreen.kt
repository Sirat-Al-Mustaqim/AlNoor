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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.siratalmustaqim.alnoor.ui.screens.settings.guard.GuardSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.prayer.PrayerSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.quran.QuranSettingsSection

@Composable
fun SettingsScreen() {
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
        QuranSettingsSection()

        // Prayer Settings Section
        PrayerSettingsSection()

        // Guard Settings Section
        GuardSettingsSection()

        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme {
        SettingsScreen()
    }
}
