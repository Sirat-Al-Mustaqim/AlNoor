package com.siratalmustaqim.alnoor.vpn

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the current state of the VPN service
 */
enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

/**
 * VPN statistics for monitoring
 */
@Parcelize
data class VpnStatistics(
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val packetsBlocked: Long = 0,
    val connectionTime: Long = 0
) : Parcelable

/**
 * Result of VPN operations
 */
sealed class VpnResult {
    data object Success : VpnResult()
    data class Error(val message: String) : VpnResult()
}
