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
    val alwaysOnVpn: Boolean = false,
    val statistics: VpnStatistics = VpnStatistics(),
    val isConnecting: Boolean = false
) {
    val isConnected: Boolean get() = vpnState == VpnState.CONNECTED
    val isDisconnected: Boolean get() = vpnState == VpnState.DISCONNECTED
    @get:StringRes
    val statusTextRes: Int get() = when (vpnState) {
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
        guardRepository.alwaysOnVpn,
        guardRepository.vpnStatistics
    ) { vpnState, alwaysOnVpn, statistics ->
        GuardUiState(
            vpnState = vpnState,
            alwaysOnVpn = alwaysOnVpn,
            statistics = statistics,
            isConnecting = vpnState == VpnState.CONNECTING
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuardUiState()
    )

    fun toggleVpn() {
        viewModelScope.launch {
            val currentState = uiState.value.vpnState
            val shouldConnect = currentState == VpnState.DISCONNECTED || currentState == VpnState.ERROR
            
            Timber.d("Toggle VPN: shouldConnect=$shouldConnect, currentState=$currentState")
            
            if (shouldConnect) {
                // Check VPN permission first
                val permissionIntent = guardRepository.prepareVpn()
                if (permissionIntent != null) {
                    Timber.d("VPN permission required")
                    _events.emit(GuardEvent.RequestVpnPermission(permissionIntent))
                    return@launch
                }
                
                // Start VPN
                val result = guardRepository.setAlwaysOnVpn(true)
                handleVpnResult(result, R.string.guard_connected_message)
            } else {
                // Stop VPN
                val result = guardRepository.setAlwaysOnVpn(false)
                handleVpnResult(result, R.string.guard_disconnected_message)
            }
        }
    }

    fun setAlwaysOnVpn(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Setting always-on VPN: $enabled")
            
            if (enabled) {
                val permissionIntent = guardRepository.prepareVpn()
                if (permissionIntent != null) {
                    _events.emit(GuardEvent.RequestVpnPermission(permissionIntent))
                    return@launch
                }
            }
            
            val result = guardRepository.setAlwaysOnVpn(enabled)
            handleVpnResult(result, if (enabled) R.string.guard_always_on_enabled else R.string.guard_always_on_disabled)
        }
    }

    fun onVpnPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                Timber.d("VPN permission granted, starting VPN")
                val result = guardRepository.setAlwaysOnVpn(true)
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
                // Note: result.message is a dynamic error, keeping it as is
                // In production, you might want to map common errors to resource IDs
                _events.emit(GuardEvent.ShowError(R.string.guard_status_error))
            }
        }
    }
}
