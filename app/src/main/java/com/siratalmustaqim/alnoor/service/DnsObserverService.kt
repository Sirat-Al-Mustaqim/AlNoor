package com.siratalmustaqim.alnoor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.siratalmustaqim.alnoor.MainActivity
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Persistent foreground service that monitors private DNS settings
 * and enforces protection when always-on is enabled.
 *
 * This service runs in the background and uses a ContentObserver
 * to detect DNS setting changes immediately.
 */
@AndroidEntryPoint
class DnsObserverService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 2001
        private const val CHANNEL_ID = "dns_observer_channel"
        private const val CHANNEL_NAME = "DNS Protection"

        private const val PRIVATE_DNS_MODE = "private_dns_mode"
        private const val PRIVATE_DNS_SPECIFIER = "private_dns_specifier"

        private val lock = Any()

        @Volatile
        var isRunning: Boolean = false
            private set

        fun start(context: Context) {
            synchronized(lock) {
                if (isRunning) {
                    Timber.d("DnsObserverService is already running, skipping start")
                    return
                }
                isRunning = true

                val intent = Intent(context, DnsObserverService::class.java)
                context.startForegroundService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, DnsObserverService::class.java)
            context.stopService(intent)
        }

        internal fun setRunning(running: Boolean) {
            synchronized(lock) {
                isRunning = running
            }
        }
    }

    @Inject
    lateinit var guardRepository: GuardRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val dnsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Timber.d("DNS setting change detected by service")
            onDnsSettingsChanged()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("DnsObserverService created")

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        registerDnsObserver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("DnsObserverService onStartCommand")
        return START_STICKY // Restart if killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        setRunning(false)
        Timber.d("DnsObserverService destroyed")
        unregisterDnsObserver()
        serviceScope.cancel()
    }

    private fun registerDnsObserver() {
        try {
            val modeUri = Settings.Global.getUriFor(PRIVATE_DNS_MODE)
            val specifierUri = Settings.Global.getUriFor(PRIVATE_DNS_SPECIFIER)

            contentResolver.registerContentObserver(modeUri, false, dnsObserver)
            contentResolver.registerContentObserver(specifierUri, false, dnsObserver)

            Timber.d("DNS ContentObserver registered in service")
        } catch (e: Exception) {
            Timber.e(e, "Failed to register DNS observer in service")
        }
    }

    private fun unregisterDnsObserver() {
        try {
            contentResolver.unregisterContentObserver(dnsObserver)
            Timber.d("DNS ContentObserver unregistered")
        } catch (e: Exception) {
            Timber.e(e, "Failed to unregister DNS observer")
        }
    }

    private fun onDnsSettingsChanged() {
        serviceScope.launch {
            try {
                guardRepository.enforceProtectionIfNeeded()
            } catch (e: Exception) {
                Timber.e(e, "Error enforcing protection from service")
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when DNS protection is active"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AlNoor Protection Active")
            .setContentText("DNS protection is enabled")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
