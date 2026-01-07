package com.siratalmustaqim.alnoor.ui.screens.settings.quran

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowSlider
import com.siratalmustaqim.alnoor.ui.components.SettingsSection

@Composable
fun QuranSettingsSection(
    modifier: Modifier = Modifier,
    viewModel: QuranSettingsViewModel = hiltViewModel()
) {
    val textSize by viewModel.ayahTextSize.collectAsState()
    val font by viewModel.ayahFont.collectAsState()

    SettingsSection(
        title = "Quran",
        icon = "📖",
        modifier = modifier
    ) {
        SettingsRowSlider(
            label = "Ayah Text Size",
            value = textSize,
            onValueChange = { viewModel.updateTextSize(it) },
            valueRange = 14f..32f,
            valueLabel = "${textSize.toInt()}sp"
        )

        SettingsRowDropdown(
            label = "Ayah Font",
            selectedValue = font,
            options = QuranSettingsViewModel.availableFonts,
            onOptionSelected = { viewModel.updateFont(it) }
        )
    }
}
