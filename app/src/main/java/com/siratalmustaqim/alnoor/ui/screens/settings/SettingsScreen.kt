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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowSlider
import com.siratalmustaqim.alnoor.ui.components.SettingsRowText
import com.siratalmustaqim.alnoor.ui.components.SettingsRowToggle
import com.siratalmustaqim.alnoor.ui.components.SettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.guard.GuardSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.prayer.PrayerSettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.quran.QuranSettingsSection
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme

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

/**
 * Preview-safe version that doesn't use Hilt ViewModels
 */
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AlNoorTheme {
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

            // Preview Quran Section
            SettingsSection(title = "Quran", icon = "📖") {
                SettingsRowSlider(
                    label = "Ayah Text Size",
                    value = 20f,
                    onValueChange = {},
                    valueRange = 14f..32f,
                    valueLabel = "20sp"
                )
                SettingsRowDropdown(
                    label = "Ayah Font",
                    selectedValue = "Amiri",
                    options = listOf("Amiri", "Scheherazade", "Noto Naskh Arabic"),
                    onOptionSelected = {}
                )
            }

            // Preview Prayer Section
            SettingsSection(title = "Prayer", icon = "🕌") {
                SettingsRowText(
                    label = "Current Location",
                    value = "Not set"
                )
                SettingsRowDropdown(
                    label = "Azan Audio",
                    selectedValue = "Default",
                    options = listOf("Default", "Makkah", "Madinah"),
                    onOptionSelected = {}
                )
            }

            // Preview Guard Section
            SettingsSection(title = "Guard", icon = "🛡️") {
                SettingsRowToggle(
                    label = "Always-On VPN",
                    checked = false,
                    onCheckedChange = {},
                    subtitle = "Keep VPN active at all times"
                )
            }
        }
    }
}
