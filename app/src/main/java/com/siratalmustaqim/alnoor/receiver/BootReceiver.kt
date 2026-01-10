package com.siratalmustaqim.alnoor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import com.siratalmustaqim.alnoor.service.DnsObserverService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Broadcast receiver that starts DnsObserverService on device boot
 * if always-on protection is enabled.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            
            Timber.d("Boot completed, checking always-on protection status")

            val pendingResult = goAsync()
            
            scope.launch {
                try {
                    val isAlwaysOn = settingsDataStore.alwaysOnProtection.first()
                    
                    if (isAlwaysOn) {
                        Timber.d("Always-on protection is enabled, starting DnsObserverService")
                        DnsObserverService.start(context)
                    } else {
                        Timber.d("Always-on protection is disabled, not starting service")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error checking always-on protection on boot")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
