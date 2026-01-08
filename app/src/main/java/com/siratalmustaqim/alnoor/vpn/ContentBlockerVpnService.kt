package com.siratalmustaqim.alnoor.vpn

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.siratalmustaqim.alnoor.BuildConfig
import com.siratalmustaqim.alnoor.MainActivity
import com.siratalmustaqim.alnoor.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * VPN Service for content filtering using DNS-based blocking.
 * 
 * This service creates a local VPN that configures the system to use
 * Cloudflare's family-safe DNS servers (1.1.1.3) for all DNS lookups.
 * 
 * How it works:
 * - The VPN captures DNS traffic only (not all internet traffic)
 * - DNS queries are resolved by Cloudflare's family-safe DNS
 * - Adult/inappropriate domains are blocked at the DNS level
 * - Regular internet traffic flows normally without going through the VPN
 */
@SuppressLint("VpnServicePolicy")
class ContentBlockerVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope: CoroutineScope? = null
    private var statisticsJob: Job? = null
    private var isRunning = false
    
    // Track connection statistics
    private var connectionStartTime = 0L
    private var queriesBlocked = 0L

    companion object {
        const val VPN_ID = "${BuildConfig.APPLICATION_ID}.vpn"
        const val ACTION_START = "$VPN_ID.START"
        const val ACTION_STOP = "$VPN_ID.STOP"

        // Broadcast actions
        const val ACTION_STATE_CHANGED = "$VPN_ID.STATE_CHANGED"
        const val ACTION_STATISTICS_CHANGED = "$VPN_ID.STATISTICS_CHANGED"

        // Extras
        const val EXTRA_STATE = "state"
        const val EXTRA_STATISTICS = "statistics"

        // Statistics update interval
        private const val STATISTICS_UPDATE_INTERVAL_MS = 1000L
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("ContentBlockerVpnService created")
        serviceScope = CoroutineScope(Dispatchers.IO)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("ContentBlockerVpnService onStartCommand")

        when (intent?.action) {
            ACTION_START -> startVpn()
            ACTION_STOP -> stopVpn()
        }

        return START_STICKY
    }

    private fun startVpn() {
        if (isRunning) {
            Timber.d("VPN already running")
            return
        }

        try {
            Timber.d("Starting VPN service")

            // Create notification channel
            createNotificationChannel()

            // Start foreground service with notification
            val notification = createNotification()
            startForeground(VpnConfig.NOTIFICATION_ID, notification)

            // Establish VPN connection
            vpnInterface = establishVpnConnection()

            if (vpnInterface != null) {
                isRunning = true
                connectionStartTime = System.currentTimeMillis()
                broadcastStateChange(VpnState.CONNECTED)

                // Start statistics broadcasting (no packet processing needed!)
                statisticsJob = serviceScope?.launch {
                    broadcastStatisticsPeriodically()
                }

                Timber.d("VPN started successfully - DNS filtering active")
            } else {
                Timber.e("Failed to establish VPN connection")
                broadcastStateChange(VpnState.ERROR)
                stopSelf()
            }

        } catch (e: Exception) {
            Timber.e(e, "Error starting VPN")
            broadcastStateChange(VpnState.ERROR)
            stopSelf()
        }
    }

    /**
     * Establish DNS-only VPN connection.
     * 
     * This VPN configures the system to use Cloudflare's family-safe DNS servers
     * for all DNS lookups. We DON'T route any traffic through the VPN tunnel -
     * the DNS queries go directly to Cloudflare through the normal network.
     * 
     * Both IPv4 and IPv6 DNS servers are configured to ensure complete coverage.
     */
    private fun establishVpnConnection(): ParcelFileDescriptor? {
        return try {
            Builder()
                .setSession(VpnConfig.SESSION_NAME)
                // Assign virtual IP addresses (both IPv4 and IPv6)
                .addAddress(VpnConfig.VPN_ADDRESS, 32)
                .addAddress(VpnConfig.VPN_ADDRESS_V6, 128)
                // NO routes! We don't want to tunnel any traffic.
                // Just set the DNS servers that the system should use.
                // IPv4 DNS (Cloudflare family-safe)
                .addDnsServer(VpnConfig.DNS_PRIMARY)    // 1.1.1.3
                .addDnsServer(VpnConfig.DNS_SECONDARY)  // 1.0.0.3
                // IPv6 DNS (Cloudflare family-safe)
                .addDnsServer(VpnConfig.DNS_PRIMARY_V6)   // 2606:4700:4700::1113
                .addDnsServer(VpnConfig.DNS_SECONDARY_V6) // 2606:4700:4700::1003
                .setMtu(VpnConfig.VPN_MTU)
                .setBlocking(false)
                .establish()
        } catch (e: Exception) {
            Timber.e(e, "Error establishing VPN")
            null
        }
    }

    private suspend fun broadcastStatisticsPeriodically() {
        while (isRunning && serviceScope?.isActive == true) {
            delay(STATISTICS_UPDATE_INTERVAL_MS)

            if (isRunning) {
                val stats = VpnStatistics(
                    bytesIn = 0,  // Not tracking packet bytes in DNS-only mode
                    bytesOut = 0,
                    packetsBlocked = queriesBlocked,
                    connectionTime = (System.currentTimeMillis() - connectionStartTime) / 1000
                )
                broadcastStatistics(stats)
            }
        }
    }

    private fun broadcastStatistics(stats: VpnStatistics) {
        val intent = Intent(ACTION_STATISTICS_CHANGED).apply {
            putExtra(EXTRA_STATISTICS, stats)
        }
        sendBroadcast(intent)
    }

    private fun stopVpn() {
        Timber.d("Stopping VPN service")

        isRunning = false

        // Cancel coroutine jobs
        statisticsJob?.cancel()
        statisticsJob = null

        // Close VPN interface
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Timber.e(e, "Error closing VPN interface")
        }
        vpnInterface = null
        connectionStartTime = 0L

        // Broadcast state change
        broadcastStateChange(VpnState.DISCONNECTED)

        // Stop foreground service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        Timber.d("VPN service stopped")
    }

    /**
     * Broadcast VPN state change to listeners
     */
    private fun broadcastStateChange(state: VpnState) {
        val intent = Intent(ACTION_STATE_CHANGED).apply {
            putExtra(EXTRA_STATE, state.name)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ContentBlockerVpnService destroyed")

        // Only stop if explicitly requested (not when killed by system)
        if (!isRunning) {
            serviceScope?.cancel()
            serviceScope = null
        }
    }

    /**
     * Called when the app is swiped from recents.
     * We restart the service to keep content filtering active.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.d("App removed from recents, restarting VPN service")
        
        if (isRunning) {
            // Schedule restart using the same intent
            val restartIntent = Intent(this, ContentBlockerVpnService::class.java).apply {
                action = ACTION_START
            }
            startForegroundService(restartIntent)
        }
    }

    override fun onRevoke() {
        super.onRevoke()
        Timber.d("VPN permission revoked")
        stopVpn()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            VpnConfig.NOTIFICATION_CHANNEL_ID,
            VpnConfig.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when content filtering VPN is active"
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

        return NotificationCompat.Builder(this, VpnConfig.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("AlNoor Content Guard Active")
            .setContentText("DNS filtering is protecting your browsing")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
