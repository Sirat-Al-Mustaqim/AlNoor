package com.siratalmustaqim.alnoor.data.repository

import android.content.Intent
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import com.siratalmustaqim.alnoor.vpn.VpnManager
import com.siratalmustaqim.alnoor.vpn.VpnResult
import com.siratalmustaqim.alnoor.vpn.VpnState
import com.siratalmustaqim.alnoor.vpn.VpnStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing Guard (VPN) functionality
 * Bridges the ViewModel with VPN service and settings
 */
@Singleton
class GuardRepository @Inject constructor(
    private val vpnManager: VpnManager,
    private val settingsDataStore: SettingsDataStore
) {

    /**
     * Get always-on protection state from settings
     */
    val alwaysOnProtection: Flow<Boolean> = settingsDataStore.alwaysOnProtection

    /**
     * Get current VPN state
     */
    val vpnState: StateFlow<VpnState> = vpnManager.vpnState

    /**
     * Get VPN statistics
     */
    val vpnStatistics: StateFlow<VpnStatistics> = vpnManager.statistics

    /**
     * Start the VPN service (immediate action)
     * @return VpnResult indicating success or error
     */
    fun startVpn(): VpnResult {
        Timber.d("Starting VPN")
        return vpnManager.startVpn()
    }

    /**
     * Stop the VPN service (immediate action)
     * @return VpnResult indicating success or error
     */
    fun stopVpn(): VpnResult {
        Timber.d("Stopping VPN")
        return vpnManager.stopVpn()
    }

    /**
     * Enable or disable always-on protection
     */
    suspend fun setAlwaysOnProtection(enabled: Boolean) {
        Timber.d("Setting always-on protection: $enabled")
        settingsDataStore.updateAlwaysOnProtection(enabled)
    }

    /**
     * Prepare VPN - check if permission is granted
     * @return Intent to request permission, or null if granted
     */
    fun prepareVpn(): Intent? {
        return vpnManager.prepareVpn()
    }

    /**
     * Check if VPN is currently connected
     */
    fun isVpnConnected(): Boolean {
        return vpnManager.isConnected()
    }
}
