package com.siratalmustaqim.alnoor.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.siratalmustaqim.alnoor.MainActivity
import com.siratalmustaqim.alnoor.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

/**
 * VPN Service for content filtering using local VPN and Cloudflare DNS
 * This service creates a local VPN tunnel and routes all DNS traffic through
 * Cloudflare's family-safe DNS servers (1.1.1.3) which block adult content
 */
class ContentBlockerVpnService : VpnService() {
    
    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope: CoroutineScope? = null
    private var vpnJob: Job? = null
    private val packetHandler = PacketHandler()
    private var isRunning = false
    
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
                
                // Start packet processing in coroutine
                vpnJob = serviceScope?.launch {
                    processPackets()
                }
                
                Timber.d("VPN started successfully")
            } else {
                Timber.e("Failed to establish VPN connection")
                stopSelf()
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Error starting VPN")
            stopSelf()
        }
    }
    
    private fun establishVpnConnection(): ParcelFileDescriptor? {
        return try {
            Builder()
                .setSession(VpnConfig.SESSION_NAME)
                .addAddress(VpnConfig.VPN_ADDRESS, 32)
                .addRoute(VpnConfig.VPN_ROUTE, VpnConfig.VPN_PREFIX_LENGTH)
                .addDnsServer(VpnConfig.DNS_PRIMARY) // Cloudflare family-safe DNS
                .addDnsServer(VpnConfig.DNS_SECONDARY)
                .setMtu(VpnConfig.VPN_MTU)
                .setBlocking(false)
                .establish()
        } catch (e: Exception) {
            Timber.e(e, "Error establishing VPN")
            null
        }
    }
    
    private suspend fun processPackets() {
        val vpnFd = vpnInterface ?: return
        val inputStream = FileInputStream(vpnFd.fileDescriptor)
        val outputStream = FileOutputStream(vpnFd.fileDescriptor)
        val packet = ByteBuffer.allocate(VpnConfig.VPN_MTU)
        
        try {
            while (isRunning && serviceScope?.isActive == true) {
                // Read packet from VPN interface
                packet.clear()
                val length = inputStream.channel.read(packet)
                
                if (length > 0) {
                    packet.flip()
                    
                    // Process the packet
                    val processedPacket = packetHandler.processPacket(packet)
                    
                    // Write processed packet back to VPN interface
                    if (processedPacket != null) {
                        processedPacket.position(0)
                        val written = outputStream.channel.write(processedPacket)
                        packetHandler.recordBytesSent(written.toLong())
                    }
                }
            }
        } catch (e: Exception) {
            if (isRunning) {
                Timber.e(e, "Error processing packets")
            }
        } finally {
            Timber.d("Packet processing stopped")
        }
    }
    
    private fun stopVpn() {
        Timber.d("Stopping VPN service")
        
        isRunning = false
        
        // Cancel coroutine job
        vpnJob?.cancel()
        vpnJob = null
        
        // Close VPN interface
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Timber.e(e, "Error closing VPN interface")
        }
        vpnInterface = null
        
        // Stop foreground service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        
        Timber.d("VPN service stopped")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ContentBlockerVpnService destroyed")
        
        stopVpn()
        
        // Cancel coroutine scope
        serviceScope?.cancel()
        serviceScope = null
    }
    
    override fun onRevoke() {
        super.onRevoke()
        Timber.d("VPN permission revoked")
        stopVpn()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            .setContentText("Your content is being filtered")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    companion object {
        const val ACTION_START = "com.siratalmustaqim.alnoor.vpn.START"
        const val ACTION_STOP = "com.siratalmustaqim.alnoor.vpn.STOP"
    }
}
