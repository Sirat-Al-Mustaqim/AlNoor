package com.siratalmustaqim.alnoor.data.repository

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import com.siratalmustaqim.alnoor.BuildConfig
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
            if (!BuildConfig.DEBUG) {
                setAdbProtection(enabled)
            }
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

                // Requires API 29+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Prevent users from configuring private DNS
                    devicePolicyManager.addUserRestriction(
                        adminComponent,
                        UserManager.DISALLOW_CONFIG_PRIVATE_DNS
                    )
                    Timber.d("Added DISALLOW_CONFIG_PRIVATE_DNS restriction")

                    // Clear private DNS by setting to opportunistic mode (automatic)
                    devicePolicyManager.setGlobalPrivateDnsModeOpportunistic(adminComponent)
                    Timber.d("Cleared private DNS - set to opportunistic mode")
                } else {
                    // TODO: need WRITE_SECURE_SETTINGS permission
                }
            } else {
                // Remove the restrictions
                devicePolicyManager.clearUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_APPS_CONTROL
                )
                Timber.d("Removed DISALLOW_APPS_CONTROL restriction")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    devicePolicyManager.clearUserRestriction(
                        adminComponent,
                        UserManager.DISALLOW_CONFIG_PRIVATE_DNS
                    )
                    Timber.d("Removed DISALLOW_CONFIG_PRIVATE_DNS restriction")
                }
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to set app protection")
        }
    }

    private fun setAdbProtection(enabled: Boolean) {
        if (!isDeviceOwner) {
            Timber.w("Cannot set ADB protection: not device owner")
            return
        }

        try {
            if (enabled) {
                // 1. Physically turn off the USB Debugging toggle in the system
                devicePolicyManager.setGlobalSetting(
                    adminComponent,
                    Settings.Global.ADB_ENABLED,
                    "0"
                )

                // 2. Block the user from enabling any debugging features
                devicePolicyManager.addUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_DEBUGGING_FEATURES
                )

                // 3. Hide the "Developer Options" menu entirely from Settings
                // This prevents turning ADB back on via the UI
                devicePolicyManager.addUserRestriction(
                    adminComponent,
                    "no_config_developer_options"  // Not a public constant in UserManager
                )

                Timber.d("Al Noor: ADB and Developer Options have been sealed.")
            } else {
                // Restore settings only after 13 Ayahs + Cooldown
                devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_DEBUGGING_FEATURES)
                devicePolicyManager.clearUserRestriction(adminComponent, "no_config_developer_options")

                // Note: You may need to manually re-enable ADB if you need it for dev work
                devicePolicyManager.setGlobalSetting(adminComponent, Settings.Global.ADB_ENABLED, "1")
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to modify ADB settings")
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
