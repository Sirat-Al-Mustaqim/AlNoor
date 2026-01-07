package com.siratalmustaqim.alnoor.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for controlling the VPN service
 * Handles VPN lifecycle and state management
 */
@Singleton
class VpnManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()
    
    private val _statistics = MutableStateFlow(VpnStatistics())
    val statistics: StateFlow<VpnStatistics> = _statistics.asStateFlow()
    
    /**
     * Prepare VPN - checks if VPN permission is granted
     * @return Intent to request VPN permission, or null if already granted
     */
    fun prepareVpn(): Intent? {
        return VpnService.prepare(context)
    }
    
    /**
     * Start the VPN service
     * @return VpnResult indicating success or error
     */
    fun startVpn(): VpnResult {
        return try {
            // Check if VPN permission is granted
            val prepareIntent = prepareVpn()
            if (prepareIntent != null) {
                Timber.w("VPN permission not granted")
                return VpnResult.Error("VPN permission required")
            }
            
            Timber.d("Starting VPN service")
            _vpnState.value = VpnState.CONNECTING
            
            val intent = Intent(context, ContentBlockerVpnService::class.java).apply {
                action = ContentBlockerVpnService.ACTION_START
            }
            
            context.startForegroundService(intent)
            
            _vpnState.value = VpnState.CONNECTED
            Timber.d("VPN service started")
            
            VpnResult.Success
        } catch (e: Exception) {
            Timber.e(e, "Failed to start VPN")
            _vpnState.value = VpnState.ERROR
            VpnResult.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Stop the VPN service
     * @return VpnResult indicating success or error
     */
    fun stopVpn(): VpnResult {
        return try {
            Timber.d("Stopping VPN service")
            _vpnState.value = VpnState.DISCONNECTING
            
            val intent = Intent(context, ContentBlockerVpnService::class.java).apply {
                action = ContentBlockerVpnService.ACTION_STOP
            }
            
            context.startService(intent)
            
            _vpnState.value = VpnState.DISCONNECTED
            Timber.d("VPN service stopped")
            
            VpnResult.Success
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop VPN")
            _vpnState.value = VpnState.ERROR
            VpnResult.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Check if VPN is currently connected
     */
    fun isConnected(): Boolean {
        return _vpnState.value == VpnState.CONNECTED
    }
    
    /**
     * Get current VPN state
     */
    fun getCurrentState(): VpnState {
        return _vpnState.value
    }
}
