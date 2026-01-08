package com.siratalmustaqim.alnoor.ui.screens.settings.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.ui.components.SettingsRowDropdown
import com.siratalmustaqim.alnoor.ui.components.SettingsRowSlider
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun QuranSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: QuranSettingsViewModel = hiltViewModel()
) {
    QuranSettingsScreenContent(
        uiStateFlow = viewModel.uiState,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuranSettingsScreenContent(
    uiStateFlow: StateFlow<QuranSettingsUiState>,
    onBackClick: () -> Unit
) {
    val uiState by uiStateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quran_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsRowSlider(
                label = "Ayah Text Size",
                value = uiState.ayahTextSize,
                onValueChange = uiState.onTextSizeChange,
                valueRange = 14f..32f,
                valueLabel = stringResource(
                    R.string.quran_settings_ayah_text_size,
                    uiState.ayahTextSize.toInt()
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsRowDropdown(
                label = "Ayah Font",
                selectedValue = uiState.ayahFont,
                options = QuranSettingsUiState.availableFonts,
                onOptionSelected = uiState.onFontChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuranSettingsScreenPreview() {
    AlNoorTheme {
        QuranSettingsScreenContent(
            uiStateFlow = MutableStateFlow(QuranSettingsUiState()),
            onBackClick = {}
        )
    }
}
