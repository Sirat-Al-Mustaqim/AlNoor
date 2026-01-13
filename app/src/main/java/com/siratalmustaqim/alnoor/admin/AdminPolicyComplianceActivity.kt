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
 * IMPORTANT: This activity MUST always return RESULT_OK to complete provisioning.
 * Any policy application errors should be logged but not prevent completion.
 *
 * For AlNoor, we apply the initial protection policies here:
 * - Restrict private DNS configuration
 * - Protect app from force stop and uninstall
 */
class AdminPolicyComplianceActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("AdminPolicyComplianceActivity started")

        // Log the intent extras for debugging
        intent.extras?.let { extras ->
            Timber.d("Compliance intent extras:")
            for (key in extras.keySet()) {
                Timber.d("  $key: ${extras.get(key)}")
            }
        }

        // Try to apply initial policies (errors won't block provisioning)
        try {
            applyInitialPolicies()
        } catch (e: Exception) {
            // Log but don't fail - provisioning must complete
            Timber.e(e, "Error in applyInitialPolicies, but continuing with provisioning")
        }

        // CRITICAL: Always mark compliance as complete to finish provisioning
        setResult(RESULT_OK)
        Timber.d("Admin policy compliance completed successfully")
        finish()
    }

    private fun applyInitialPolicies() {
        val dpm = getSystemService(DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        if (dpm == null) {
            Timber.w("Could not get DevicePolicyManager")
            return
        }

        val adminComponent = AlNoorDeviceAdminReceiver.getComponentName(this)

        // Check device owner status - during provisioning this may not be set yet
        val isDeviceOwner = try {
            dpm.isDeviceOwnerApp(packageName)
        } catch (e: Exception) {
            Timber.w(e, "Error checking device owner status")
            false
        }

        if (!isDeviceOwner) {
            Timber.w("Not device owner yet, policies will be applied later")
            return
        }

        Timber.d("Applying initial device policies...")

        // Each policy is wrapped in its own try-catch to apply as many as possible
        try {
            dpm.setGlobalPrivateDnsModeOpportunistic(adminComponent)
            Timber.d("Private DNS set to opportunistic mode")
        } catch (e: Exception) {
            Timber.w(e, "Failed to set private DNS mode")
        }

        try {
            dpm.setUninstallBlocked(adminComponent, packageName, true)
            Timber.d("App uninstall blocked")
        } catch (e: Exception) {
            Timber.w(e, "Failed to block uninstall")
        }

        try {
            dpm.setKeepUninstalledPackages(adminComponent, listOf(packageName))
            Timber.d("Keep uninstalled packages set")
        } catch (e: Exception) {
            Timber.w(e, "Failed to set keep uninstalled packages")
        }

        Timber.d("Initial device policies applied (with possible partial failures)")
    }
}
