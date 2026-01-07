package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import com.siratalmustaqim.alnoor.vpn.VpnResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class GuardSettingsUiState(
    val vpnEnabled: Boolean = false,
    val onVpnToggle: (Boolean) -> Unit = {},
    val alwaysOnProtection: Boolean = false,
    val onAlwaysOnProtectionToggle: (Boolean) -> Unit = {},
    val offlineMode: Boolean = false,
    val onOfflineModeToggle: (Boolean) -> Unit = {}
)

sealed class GuardSettingsEvent {
    data class RequestVpnPermission(val intent: Intent) : GuardSettingsEvent()
    data class ShowError(val message: String) : GuardSettingsEvent()
}

@HiltViewModel
class GuardSettingsViewModel @Inject constructor(
    private val guardRepository: GuardRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<GuardSettingsEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<GuardSettingsUiState> = combine(
        guardRepository.vpnEnabled,
        guardRepository.alwaysOnProtection,
        guardRepository.offlineMode
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
            Timber.d("Toggling VPN: $enabled")
            
            if (enabled) {
                // Check VPN permission first
                val permissionIntent = guardRepository.prepareVpn()
                if (permissionIntent != null) {
                    Timber.d("VPN permission required")
                    _events.emit(GuardSettingsEvent.RequestVpnPermission(permissionIntent))
                    return@launch
                }
            }
            
            // Enable or disable VPN
            val result = guardRepository.setVpnEnabled(enabled)
            
            when (result) {
                is VpnResult.Success -> {
                    Timber.d("VPN toggled successfully")
                }
                is VpnResult.Error -> {
                    Timber.e("Failed to toggle VPN: ${result.message}")
                    _events.emit(GuardSettingsEvent.ShowError(result.message))
                }
            }
        }
    }

    private fun toggleAlwaysOnProtection(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Toggling always-on protection: $enabled")
            guardRepository.setAlwaysOnProtection(enabled)
        }
    }

    private fun toggleOfflineMode(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Toggling offline mode: $enabled")
            guardRepository.setOfflineMode(enabled)
        }
    }
}
