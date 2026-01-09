package com.siratalmustaqim.alnoor.ui.screens.guard

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import com.siratalmustaqim.alnoor.vpn.VpnResult
import com.siratalmustaqim.alnoor.vpn.VpnState
import com.siratalmustaqim.alnoor.vpn.VpnStatistics
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

data class GuardUiState(
    val vpnState: VpnState = VpnState.DISCONNECTED,
    val statistics: VpnStatistics = VpnStatistics(),
    val alwaysOnProtection: Boolean = false,
    val isDeviceOwner: Boolean = false,
    val isConnecting: Boolean = false,
    val onToggleVpn: () -> Unit = {},
    val onAlwaysOnProtectionChange: (Boolean) -> Unit = {}
) {
    val isConnected: Boolean get() = vpnState == VpnState.CONNECTED
    val isDisconnected: Boolean get() = vpnState == VpnState.DISCONNECTED

    @get:StringRes
    val statusTextRes: Int
        get() = when (vpnState) {
            VpnState.DISCONNECTED -> R.string.guard_status_protection_off
            VpnState.CONNECTING -> R.string.guard_status_connecting
            VpnState.CONNECTED -> R.string.guard_status_protected
            VpnState.DISCONNECTING -> R.string.guard_status_disconnecting
            VpnState.ERROR -> R.string.guard_status_error
        }
}

sealed class GuardEvent {
    data class RequestVpnPermission(val intent: Intent) : GuardEvent()
    data class ShowError(@param:StringRes val messageRes: Int) : GuardEvent()
    data class ShowMessage(@param:StringRes val messageRes: Int) : GuardEvent()
}

@HiltViewModel
class GuardViewModel @Inject constructor(
    private val guardRepository: GuardRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<GuardEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<GuardUiState> = combine(
        guardRepository.vpnState,
        guardRepository.vpnStatistics,
        guardRepository.alwaysOnProtection
    ) { vpnState, statistics, alwaysOnProtection ->
        GuardUiState(
            vpnState = vpnState,
            statistics = statistics,
            alwaysOnProtection = alwaysOnProtection,
            isDeviceOwner = guardRepository.isDeviceOwner,
            isConnecting = vpnState == VpnState.CONNECTING,
            onToggleVpn = ::toggleVpn,
            onAlwaysOnProtectionChange = ::setAlwaysOnProtection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuardUiState(
            isDeviceOwner = guardRepository.isDeviceOwner,
            onToggleVpn = ::toggleVpn,
            onAlwaysOnProtectionChange = ::setAlwaysOnProtection
        )
    )

    private fun toggleVpn() {
        viewModelScope.launch {
            val currentState = uiState.value.vpnState
            val alwaysOnEnabled = uiState.value.alwaysOnProtection
            val shouldConnect =
                currentState == VpnState.DISCONNECTED || currentState == VpnState.ERROR

            Timber.d("Toggle VPN: shouldConnect=$shouldConnect, currentState=$currentState, alwaysOn=$alwaysOnEnabled")

            if (shouldConnect) {
                // Check VPN permission first
                val permissionIntent = guardRepository.prepareVpn()
                if (permissionIntent != null) {
                    Timber.d("VPN permission required")
                    _events.emit(GuardEvent.RequestVpnPermission(permissionIntent))
                    return@launch
                }

                // Start VPN (not always-on, just immediate start)
                val result = guardRepository.startVpn()
                handleVpnResult(result, R.string.guard_connected_message)
            } else {
                // Check if always-on protection is enabled - prevent stopping
                if (alwaysOnEnabled) {
                    Timber.d("Cannot stop VPN: always-on protection is enabled")
                    _events.emit(GuardEvent.ShowError(R.string.guard_cannot_stop_always_on))
                    return@launch
                }

                // Stop VPN
                val result = guardRepository.stopVpn()
                handleVpnResult(result, R.string.guard_disconnected_message)
            }
        }
    }



    private fun setAlwaysOnProtection(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Setting always-on protection: $enabled")
            guardRepository.setAlwaysOnProtection(enabled)
        }
    }

    fun onVpnPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                Timber.d("VPN permission granted, starting VPN")
                val result = guardRepository.startVpn()
                handleVpnResult(result, R.string.guard_connected_message)
            } else {
                Timber.d("VPN permission denied")
                _events.emit(GuardEvent.ShowError(R.string.guard_vpn_permission_required))
            }
        }
    }

    private suspend fun handleVpnResult(result: VpnResult, @StringRes successMessageRes: Int) {
        when (result) {
            is VpnResult.Success -> {
                Timber.d("VPN operation successful")
                _events.emit(GuardEvent.ShowMessage(successMessageRes))
            }

            is VpnResult.Error -> {
                Timber.e("VPN error: ${result.message}")
                _events.emit(GuardEvent.ShowError(R.string.guard_status_error))
            }
        }
    }
}
