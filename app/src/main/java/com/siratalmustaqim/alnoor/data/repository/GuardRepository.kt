package com.siratalmustaqim.alnoor.data.repository

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserManager
import com.siratalmustaqim.alnoor.admin.AlNoorDeviceAdminReceiver
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import com.siratalmustaqim.alnoor.vpn.VpnManager
import com.siratalmustaqim.alnoor.vpn.VpnResult
import com.siratalmustaqim.alnoor.vpn.VpnState
import com.siratalmustaqim.alnoor.vpn.VpnStatistics
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val settingsDataStore: SettingsDataStore,
    @param:ApplicationContext private val context: Context
) {
    private val devicePolicyManager: DevicePolicyManager by lazy {
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val adminComponent: ComponentName by lazy {
        AlNoorDeviceAdminReceiver.getComponentName(context)
    }

    /**
     * Check if the app is device owner (for always-on protection)
     */
    val isDeviceOwner: Boolean by lazy {
        devicePolicyManager.isDeviceOwnerApp(context.packageName)
    }

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
     * Also applies DPM restrictions when enabled (if device owner)
     */
    suspend fun setAlwaysOnProtection(enabled: Boolean) {
        Timber.d("Setting always-on protection: $enabled")
        settingsDataStore.updateAlwaysOnProtection(enabled)
        
        // Apply DPM protection if device owner
        if (isDeviceOwner) {
            setAppProtection(enabled)
        }
    }

    /**
     * Enable or disable app protection using Device Policy Manager
     * This makes Force Stop and Clear Data buttons unclickable in Settings
     * and sets the VPN as always-on
     */
    private fun setAppProtection(enabled: Boolean) {
        if (!isDeviceOwner) {
            Timber.w("Cannot set app protection: not device owner")
            return
        }

        try {
            // Set this app as the always-on VPN package
            devicePolicyManager.setAlwaysOnVpnPackage(
                adminComponent,
                if (enabled) context.packageName else null,
                enabled // lockdownEnabled - block all network traffic if VPN is not connected
            )
            Timber.d("Always-on VPN package set: ${if (enabled) context.packageName else "null"}")

            // Block uninstall of this app
            devicePolicyManager.setUninstallBlocked(
                adminComponent,
                context.packageName,
                enabled
            )
            Timber.d("Uninstall blocked: $enabled")

            // Add/remove user restrictions to prevent force stop and clear data
            if (enabled) {
                // Prevent users from modifying apps (includes force stop, clear data)
                devicePolicyManager.addUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_APPS_CONTROL
                )
                Timber.d("Added DISALLOW_APPS_CONTROL restriction")
            } else {
                // Remove the restriction
                devicePolicyManager.clearUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_APPS_CONTROL
                )
                Timber.d("Removed DISALLOW_APPS_CONTROL restriction")
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to set app protection")
        }
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
