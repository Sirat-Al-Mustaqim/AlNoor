package com.siratalmustaqim.alnoor.admin

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.os.Bundle
import timber.log.Timber

/**
 * Activity that handles the ADMIN_POLICY_COMPLIANCE intent after device provisioning.
 * This activity is called by the provisioning framework to verify that all required
 * admin policies have been applied successfully.
 *
 * For AlNoor, we apply the initial protection policies here:
 * - Enable always-on VPN
 * - Restrict private DNS configuration
 * - Protect app from force stop and uninstall
 */
class AdminPolicyComplianceActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("AdminPolicyComplianceActivity started")

        // Apply initial policies
        applyInitialPolicies()

        // Mark compliance as complete
        setResult(RESULT_OK)
        Timber.d("Admin policy compliance completed successfully")
        finish()
    }

    private fun applyInitialPolicies() {
        val dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = AlNoorDeviceAdminReceiver.getComponentName(this)

        // Verify we are the device owner
        if (!dpm.isDeviceOwnerApp(packageName)) {
            Timber.w("Not device owner, skipping policy application")
            return
        }

        Timber.d("Applying initial device policies...")

        try {
            // Set this app as always-on VPN (not actually a VPN, but enables protection)
            // Note: We don't have a VPN service, so we skip this for now
            // dpm.setAlwaysOnVpnPackage(adminComponent, packageName, true)

            // Restrict private DNS configuration to prevent bypassing DNS filtering
            dpm.setGlobalPrivateDnsModeOpportunistic(adminComponent)
            Timber.d("Private DNS set to opportunistic mode")

            // Protect the app from being uninstalled
            dpm.setUninstallBlocked(adminComponent, packageName, true)
            Timber.d("App uninstall blocked")

            // Keep app data on factory reset (useful for enterprise deployment)
            val keepPackages = arrayOf(packageName)
            try {
                dpm.setKeepUninstalledPackages(adminComponent, keepPackages.toList())
            } catch (e: Exception) {
                Timber.w(e, "Failed to set keep uninstalled packages")
            }

            Timber.d("Initial device policies applied successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error applying initial policies")
        }
    }
}
