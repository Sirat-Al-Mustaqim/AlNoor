package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuardSettingsUiState(
    val vpnEnabled: Boolean = false,
    val onVpnToggle: (Boolean) -> Unit = {},
    val alwaysOnProtection: Boolean = false,
    val onAlwaysOnProtectionToggle: (Boolean) -> Unit = {},
    val offlineMode: Boolean = false,
    val onOfflineModeToggle: (Boolean) -> Unit = {}
)

@HiltViewModel
class GuardSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<GuardSettingsUiState> = combine(
        settingsDataStore.vpnEnabled,
        settingsDataStore.alwaysOnProtection,
        settingsDataStore.offlineMode
    ) { vpnEnabled, alwaysOnProtection, offlineMode ->
        GuardSettingsUiState(
            vpnEnabled = vpnEnabled,
            onVpnToggle = ::toggleVpn,
            alwaysOnProtection = alwaysOnProtection,
            onAlwaysOnProtectionToggle = ::toggleAlwaysOnProtection,
            offlineMode = offlineMode,
            onOfflineModeToggle = ::toggleOfflineMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuardSettingsUiState(
            onVpnToggle = ::toggleVpn,
            onAlwaysOnProtectionToggle = ::toggleAlwaysOnProtection,
            onOfflineModeToggle = ::toggleOfflineMode
        )
    )

    private fun toggleVpn(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateVpnEnabled(enabled)
        }
    }

    private fun toggleAlwaysOnProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateAlwaysOnProtection(enabled)
        }
    }

    private fun toggleOfflineMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateOfflineMode(enabled)
        }
    }
}
