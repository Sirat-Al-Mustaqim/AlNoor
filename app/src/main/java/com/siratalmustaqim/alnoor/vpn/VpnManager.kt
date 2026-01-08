package com.siratalmustaqim.alnoor.vpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for controlling the VPN service
 * Handles VPN lifecycle and state management
 */
@Singleton
class VpnManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    private val _statistics = MutableStateFlow(VpnStatistics())
    val statistics: StateFlow<VpnStatistics> = _statistics.asStateFlow()

    private var connectionStartTime: Long = 0L

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ContentBlockerVpnService.ACTION_STATE_CHANGED -> {
                    val stateName = intent.getStringExtra(ContentBlockerVpnService.EXTRA_STATE)
                    val state = try {
                        VpnState.valueOf(stateName ?: VpnState.DISCONNECTED.name)
                    } catch (_: Exception) {
                        VpnState.DISCONNECTED
                    }

                    Timber.d("VPN state changed via broadcast: $state")
                    updateState(state)
                }

                ContentBlockerVpnService.ACTION_STATISTICS_CHANGED -> {
                    val stats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            ContentBlockerVpnService.EXTRA_STATISTICS,
                            VpnStatistics::class.java
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra<VpnStatistics>(
                            ContentBlockerVpnService.EXTRA_STATISTICS
                        )
                    }
                    if (stats != null) {
                        updateStatistics(stats)
                    }
                }
            }
        }
    }

    init {
        registerReceivers()
    }

    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(ContentBlockerVpnService.ACTION_STATE_CHANGED)
            addAction(ContentBlockerVpnService.ACTION_STATISTICS_CHANGED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(stateReceiver, filter)
        }

        Timber.d("VPN state receiver registered")
    }

    private fun updateState(state: VpnState) {
        _vpnState.value = state

        when (state) {
            VpnState.CONNECTED -> {
                connectionStartTime = System.currentTimeMillis() / 1000
                Timber.d("VPN connected, tracking start time: $connectionStartTime")
            }

            VpnState.DISCONNECTED -> {
                connectionStartTime = 0L
                _statistics.value = VpnStatistics()
                Timber.d("VPN disconnected, reset statistics")
            }

            else -> { /* no action */
            }
        }
    }

    private fun updateStatistics(stats: VpnStatistics) {
        val connectionTime = if (connectionStartTime > 0) {
            System.currentTimeMillis() / 1000 - connectionStartTime
        } else {
            0L
        }

        _statistics.value = stats.copy(connectionTime = connectionTime)

        Timber.v("Statistics updated: in=${stats.bytesIn}, out=${stats.bytesOut}, blocked=${stats.packetsBlocked}, time=$connectionTime")
    }

    /**
     * Prepare VPN - checks if VPN permission is granted
     * @return Intent to request VPN permission, or null if already granted
     */
    fun prepareVpn(): Intent? {
        return VpnService.prepare(context)
    }

    /**
     * Start the VPN service
     * @return VpnResult indicating success or error
     */
    fun startVpn(): VpnResult {
        return try {
            // Check if VPN permission is granted
            val prepareIntent = prepareVpn()
            if (prepareIntent != null) {
                Timber.w("VPN permission not granted")
                return VpnResult.Error("VPN permission required")
            }

            Timber.d("Starting VPN service")
            _vpnState.value = VpnState.CONNECTING

            val intent = Intent(context, ContentBlockerVpnService::class.java).apply {
                action = ContentBlockerVpnService.ACTION_START
            }

            context.startForegroundService(intent)

            Timber.d("VPN service start initiated")

            VpnResult.Success
        } catch (e: Exception) {
            Timber.e(e, "Failed to start VPN")
            _vpnState.value = VpnState.ERROR
            VpnResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Stop the VPN service
     * @return VpnResult indicating success or error
     */
    fun stopVpn(): VpnResult {
        return try {
            Timber.d("Stopping VPN service")
            _vpnState.value = VpnState.DISCONNECTING

            val intent = Intent(context, ContentBlockerVpnService::class.java).apply {
                action = ContentBlockerVpnService.ACTION_STOP
            }

            context.startService(intent)

            Timber.d("VPN service stop initiated")

            VpnResult.Success
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop VPN")
            _vpnState.value = VpnState.ERROR
            VpnResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Check if VPN is currently connected
     */
    fun isConnected(): Boolean {
        return _vpnState.value == VpnState.CONNECTED
    }
}
