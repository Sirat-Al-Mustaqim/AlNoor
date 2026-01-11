package com.siratalmustaqim.alnoor.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service that acts as a heartbeat for the Guard service.
 * When receiving a RESTART_GUARD action, it will start the DnsObserverService
 * if always-on protection is enabled.
 */
@AndroidEntryPoint
class GuardMessagingService : FirebaseMessagingService() {

    companion object {
        const val TOPIC_GUARD_CHECK = "guard_check"
        private const val ACTION_KEY = "action"
        private const val ACTION_RESTART_GUARD = "RESTART_GUARD"
    }

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM token refreshed: $token")
        // Token is not needed since we use topic subscription
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("FCM message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val action = data[ACTION_KEY]

        Timber.d("FCM action: $action")

        when (action) {
            ACTION_RESTART_GUARD -> handleRestartGuard()
            else -> Timber.d("Unknown FCM action: $action")
        }
    }

    private fun handleRestartGuard() {
        Timber.d("Handling RESTART_GUARD action")

        serviceScope.launch {
            try {
                val isAlwaysOn = settingsDataStore.alwaysOnProtection.first()

                if (isAlwaysOn) {
                    Timber.d("Always-on protection is enabled, starting DnsObserverService")
                    DnsObserverService.start(applicationContext)
                } else {
                    Timber.d("Always-on protection is disabled, ignoring RESTART_GUARD")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling RESTART_GUARD")
            }
        }
    }
}
