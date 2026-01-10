package com.siratalmustaqim.alnoor.data.repository

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.UserManager
import com.siratalmustaqim.alnoor.admin.AlNoorDeviceAdminReceiver
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import com.siratalmustaqim.alnoor.service.DnsObserverService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing Guard (Private DNS) functionality
 * Uses Device Policy Manager to set system-wide private DNS for content filtering
 */
@Singleton
class GuardRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    @param:ApplicationContext private val context: Context
) {
    companion object {
        // Cloudflare Family DNS for content filtering
        private const val FAMILY_DNS_HOST = "family.cloudflare-dns.com"

        // Global settings keys for private DNS
        private const val PRIVATE_DNS_MODE = "private_dns_mode"
        private const val PRIVATE_DNS_SPECIFIER = "private_dns_specifier"
    }

    private val devicePolicyManager: DevicePolicyManager by lazy {
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val adminComponent: ComponentName by lazy {
        AlNoorDeviceAdminReceiver.getComponentName(context)
    }

    private val _protectionEnabled = MutableStateFlow(false)

    /**
     * Current protection state (private DNS enabled)
     */
    val protectionEnabled: StateFlow<Boolean> = _protectionEnabled.asStateFlow()

    /**
     * Check if the app is device owner (required for private DNS control)
     */
    val isDeviceOwner: Boolean by lazy {
        devicePolicyManager.isDeviceOwnerApp(context.packageName)
    }

    /**
     * Get always-on protection state from settings
     */
    val alwaysOnProtection: Flow<Boolean> = settingsDataStore.alwaysOnProtection

    init {
        // Check current DNS state on init
        updateProtectionState()
    }

    /**
     * Check and enforce DNS protection if always-on is enabled
     * Called by DnsObserverService and WorkManager
     */
    suspend fun enforceProtectionIfNeeded() {
        val isAlwaysOn = settingsDataStore.alwaysOnProtection.first()
        
        if (isAlwaysOn && isDeviceOwner) {
            // Check if DNS was changed away from our settings
            val currentHost = devicePolicyManager.getGlobalPrivateDnsHost(adminComponent)
            val currentMode = devicePolicyManager.getGlobalPrivateDnsMode(adminComponent)
            
            val isProtected = currentMode == DevicePolicyManager.PRIVATE_DNS_MODE_PROVIDER_HOSTNAME 
                    && currentHost == FAMILY_DNS_HOST
            
            if (!isProtected) {
                Timber.w("DNS was changed while always-on is enabled! Reverting...")
                enableProtection()
            }
        } else {
            // Just update the protection state
            updateProtectionState()
        }
    }

    /**
     * Update protection state based on current private DNS configuration
     */
    private fun updateProtectionState() {
        if (!isDeviceOwner) {
            _protectionEnabled.value = false
            return
        }

        try {
            val mode = devicePolicyManager.getGlobalPrivateDnsMode(adminComponent)
            val host = devicePolicyManager.getGlobalPrivateDnsHost(adminComponent)
            
            // Protection is enabled only if mode is hostname AND host is our family DNS
            _protectionEnabled.value = mode == DevicePolicyManager.PRIVATE_DNS_MODE_PROVIDER_HOSTNAME 
                    && host == FAMILY_DNS_HOST
            Timber.d("Protection state updated: ${_protectionEnabled.value}, mode: $mode, host: $host")
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to get private DNS mode")
            _protectionEnabled.value = false
        }
    }

    /**
     * Enable content filtering by setting private DNS to Cloudflare Family
     * Must be called from a background thread
     * @return true if successful, false otherwise
     */
    suspend fun enableProtection(): Boolean = withContext(Dispatchers.IO) {
        if (!isDeviceOwner) {
            Timber.w("Cannot enable protection: not device owner")
            return@withContext false
        }

        try {
            Timber.d("Enabling protection - setting private DNS to $FAMILY_DNS_HOST")
            val result = devicePolicyManager.setGlobalPrivateDnsModeSpecifiedHost(
                adminComponent,
                FAMILY_DNS_HOST
            )

            // Also set via global settings as backup
            Timber.d("Set private DNS via global settings as backup")
            devicePolicyManager.setGlobalSetting(adminComponent, PRIVATE_DNS_MODE, "hostname")
            devicePolicyManager.setGlobalSetting(adminComponent, PRIVATE_DNS_SPECIFIER, FAMILY_DNS_HOST)

            val success = result == DevicePolicyManager.PRIVATE_DNS_SET_NO_ERROR
            if (success) {
                _protectionEnabled.value = true
                Timber.d("Private DNS set successfully")
            } else {
                Timber.e("Failed to set private DNS, error code: $result")
            }
            success
        } catch (e: Exception) {
            Timber.e(e, "Error setting private DNS")
            false
        }
    }

    /**
     * Disable content filtering by setting private DNS to opportunistic mode
     * Must be called from a background thread
     */
    suspend fun disableProtection() = withContext(Dispatchers.IO) {
        if (!isDeviceOwner) {
            Timber.w("Cannot disable protection: not device owner")
            return@withContext
        }

        try {
            Timber.d("Disabling protection - setting private DNS to opportunistic mode")
            devicePolicyManager.setGlobalPrivateDnsModeOpportunistic(adminComponent)

            // Also reset via global settings
            Timber.d("Reset private DNS via global settings")
            devicePolicyManager.setGlobalSetting(adminComponent, PRIVATE_DNS_MODE, "opportunistic")
            devicePolicyManager.setGlobalSetting(adminComponent, PRIVATE_DNS_SPECIFIER, "")

            _protectionEnabled.value = false
            Timber.d("Private DNS cleared successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing private DNS")
        }
    }

    /**
     * Toggle protection state
     * @return true if protection is now enabled, false otherwise
     */
    suspend fun toggleProtection(): Boolean {
        return if (_protectionEnabled.value) {
            disableProtection()
            false
        } else {
            enableProtection()
        }
    }

    /**
     * Enable or disable always-on protection
     * Also applies DPM restrictions when enabled (if device owner)
     */
    suspend fun setAlwaysOnProtection(enabled: Boolean) {
        Timber.d("Setting always-on protection: $enabled")

        // Enable protection first if enabling always-on
        if (enabled) {
            Timber.d("Enabling protection before setting always-on")
            enableProtection()
        }

        settingsDataStore.updateAlwaysOnProtection(enabled)

        // Start or stop the DNS observer service
        if (enabled) {
            DnsObserverService.start(context)
            Timber.d("Started DNS observer service")
        } else {
            DnsObserverService.stop(context)
            Timber.d("Stopped DNS observer service")
        }

        // Apply DPM protection if device owner
        if (isDeviceOwner) {
            withContext(Dispatchers.IO) {
                setAppProtection(enabled)
            }
        }
    }

    /**
     * Enable or disable app protection using Device Policy Manager
     * This makes Force Stop and Clear Data buttons unclickable in Settings
     * and locks private DNS configuration
     */
    private fun setAppProtection(enabled: Boolean) {
        if (!isDeviceOwner) {
            Timber.w("Cannot set app protection: not device owner")
            return
        }

        try {
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

                // Prevent users from configuring private DNS
                devicePolicyManager.addUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_CONFIG_PRIVATE_DNS
                )
                Timber.d("Added DISALLOW_CONFIG_PRIVATE_DNS restriction")
            } else {
                // Remove the restrictions
                devicePolicyManager.clearUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_APPS_CONTROL
                )
                Timber.d("Removed DISALLOW_APPS_CONTROL restriction")

                devicePolicyManager.clearUserRestriction(
                    adminComponent,
                    UserManager.DISALLOW_CONFIG_PRIVATE_DNS
                )
                Timber.d("Removed DISALLOW_CONFIG_PRIVATE_DNS restriction")
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to set app protection")
        }
    }
}
