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
     * Get always-on VPN state from settings (auto-start on boot)
     */
    val alwaysOnVpn: Flow<Boolean> = settingsDataStore.alwaysOnVpn

    /**
     * Get always-on protection state from settings
     */
    val alwaysOnProtection: Flow<Boolean> = settingsDataStore.alwaysOnProtection

    /**
     * Get offline mode state from settings
     */
    val offlineMode: Flow<Boolean> = settingsDataStore.offlineMode

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
     * Set always-on VPN preference (auto-start on device boot)
     * If enabling, also starts VPN immediately
     * If disabling, does NOT stop VPN - just changes boot behavior
     */
    suspend fun setAlwaysOnVpn(enabled: Boolean): VpnResult {
        Timber.d("Setting always-on VPN preference: $enabled")
        
        // Update settings preference
        settingsDataStore.updateAlwaysOnVpn(enabled)

        // If enabling, also start VPN immediately
        if (enabled) {
            return vpnManager.startVpn()
        }
        
        // If disabling, don't stop VPN - just save the preference
        return VpnResult.Success
    }

    /**
     * Enable or disable always-on protection
     */
    suspend fun setAlwaysOnProtection(enabled: Boolean) {
        Timber.d("Setting always-on protection: $enabled")
        settingsDataStore.updateAlwaysOnProtection(enabled)
    }

    /**
     * Enable or disable offline mode
     * Offline mode blocks all internet traffic
     */
    suspend fun setOfflineMode(enabled: Boolean) {
        Timber.d("Setting offline mode: $enabled")
        settingsDataStore.updateOfflineMode(enabled)
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
