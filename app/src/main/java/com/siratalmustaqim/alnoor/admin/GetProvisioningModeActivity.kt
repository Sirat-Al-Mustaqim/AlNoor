package com.siratalmustaqim.alnoor.admin

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import timber.log.Timber

/**
 * Activity that handles the GET_PROVISIONING_MODE intent during device provisioning.
 * This activity is called by the provisioning framework to determine whether the
 * device should be set up as a Device Owner (fully managed device) or Profile Owner
 * (work profile).
 *
 * For AlNoor, we always request Device Owner mode to enable full content filtering
 * protection at the device level.
 */
class GetProvisioningModeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("GetProvisioningModeActivity started")

        handleProvisioningModeRequest()
    }

    private fun handleProvisioningModeRequest() {
        val intent = intent

        // Log the provisioning extras for debugging
        intent.extras?.let { extras ->
            Timber.d("Provisioning extras received:")
            for (key in extras.keySet()) {
                Timber.d("  $key: ${extras.get(key)}")
            }
        }

        // Create result intent with provisioning mode
        val resultIntent = Intent().apply {
            // Request Device Owner mode for full device management
            // This enables all DPM features including:
            // - Always-on VPN enforcement
            // - Private DNS restriction
            // - App uninstall protection
            // - Force stop/clear data protection
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_MODE,
                DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE
            )

            // Skip user consent screen if allowed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                putExtra(
                    DevicePolicyManager.EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS,
                    true
                )
            }
        }

        Timber.d("Returning provisioning mode: FULLY_MANAGED_DEVICE")
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
