package com.siratalmustaqim.alnoor.ui.screens.settings.quran

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowSlider
import com.siratalmustaqim.alnoor.ui.components.SettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.QuranSettingsUiState

@Composable
fun QuranSettingsSection(
    uiState: QuranSettingsUiState,
    modifier: Modifier = Modifier
) {
    SettingsSection(
        title = "Quran",
        icon = "📖",
        modifier = modifier
    ) {
        SettingsRowSlider(
            label = "Ayah Text Size",
            value = uiState.ayahTextSize,
            onValueChange = uiState.onTextSizeChange,
            valueRange = 14f..32f,
            valueLabel = "${uiState.ayahTextSize.toInt()}sp"
        )

        SettingsRowDropdown(
            label = "Ayah Font",
            selectedValue = uiState.ayahFont,
            options = QuranSettingsUiState.availableFonts,
            onOptionSelected = uiState.onFontChange
        )
    }
}
