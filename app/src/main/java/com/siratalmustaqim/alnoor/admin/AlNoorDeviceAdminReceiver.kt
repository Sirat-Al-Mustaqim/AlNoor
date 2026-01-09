package com.siratalmustaqim.alnoor.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Device Admin Receiver for AlNoor app
 * Required for Device Owner functionality to enable always-on protection features
 */
class AlNoorDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Timber.d("Device Admin enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Timber.d("Device Admin disabled")
    }

    companion object {
        fun getComponentName(context: Context): android.content.ComponentName {
            return android.content.ComponentName(context, AlNoorDeviceAdminReceiver::class.java)
        }
    }
}
