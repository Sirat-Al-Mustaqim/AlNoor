package com.siratalmustaqim.alnoor.ui.screens.guard

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val statusText: String get() = when (vpnState) {
        VpnState.DISCONNECTED -> "Protection Off"
        VpnState.CONNECTING -> "Connecting..."
        VpnState.CONNECTED -> "Protected"
        VpnState.DISCONNECTING -> "Disconnecting..."
        VpnState.ERROR -> "Connection Error"
    }
}

sealed class GuardEvent {
    data class RequestVpnPermission(val intent: Intent) : GuardEvent()
    data class ShowError(val message: String) : GuardEvent()
    data class ShowMessage(val message: String) : GuardEvent()
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
                handleVpnResult(result, "Connected")
            } else {
                // Stop VPN
                val result = guardRepository.setAlwaysOnVpn(false)
                handleVpnResult(result, "Disconnected")
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
            handleVpnResult(result, if (enabled) "Always-on VPN enabled" else "Always-on VPN disabled")
        }
    }

    fun onVpnPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                Timber.d("VPN permission granted, starting VPN")
                val result = guardRepository.setAlwaysOnVpn(true)
                handleVpnResult(result, "Connected")
            } else {
                Timber.d("VPN permission denied")
                _events.emit(GuardEvent.ShowError("VPN permission is required"))
            }
        }
    }

    private suspend fun handleVpnResult(result: VpnResult, successMessage: String) {
        when (result) {
            is VpnResult.Success -> {
                Timber.d(successMessage)
                _events.emit(GuardEvent.ShowMessage(successMessage))
            }
            is VpnResult.Error -> {
                Timber.e("VPN error: ${result.message}")
                _events.emit(GuardEvent.ShowError(result.message))
            }
        }
    }
}
