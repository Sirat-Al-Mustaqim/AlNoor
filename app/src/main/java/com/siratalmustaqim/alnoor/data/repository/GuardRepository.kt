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
     * Get VPN enabled state from settings
     */
    val vpnEnabled: Flow<Boolean> = settingsDataStore.vpnEnabled
    
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
     * Enable or disable VPN
     * @param enabled Whether to enable VPN
     * @return VpnResult indicating success or error
     */
    suspend fun setVpnEnabled(enabled: Boolean): VpnResult {
        Timber.d("Setting VPN enabled: $enabled")
        
        // Update settings first
        settingsDataStore.updateVpnEnabled(enabled)
        
        // Start or stop VPN service
        return if (enabled) {
            vpnManager.startVpn()
        } else {
            vpnManager.stopVpn()
        }
    }
    
    /**
     * Enable or disable always-on protection
     */
    suspend fun setAlwaysOnProtection(enabled: Boolean) {
        Timber.d("Setting always-on protection: $enabled")
        settingsDataStore.updateAlwaysOnProtection(enabled)
        
        // If always-on is enabled and VPN is enabled, ensure VPN stays running
        // This could be enhanced with additional logic to prevent VPN from being disabled
    }
    
    /**
     * Enable or disable offline mode
     * Offline mode blocks all internet traffic
     */
    suspend fun setOfflineMode(enabled: Boolean) {
        Timber.d("Setting offline mode: $enabled")
        settingsDataStore.updateOfflineMode(enabled)
        
        // Offline mode would require additional packet filtering logic
        // For now, we just store the preference
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
