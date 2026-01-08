package com.siratalmustaqim.alnoor.vpn

/**
 * Configuration for the local VPN tunnel
 * Uses Cloudflare's family-safe DNS resolver for adult content filtering
 */
object VpnConfig {
    // Virtual network interface configuration
    const val VPN_ADDRESS = "10.0.0.2"
    const val VPN_ADDRESS_V6 = "fd00::2"
    const val VPN_MTU = 1500

    // Cloudflare family-safe DNS (blocks adult content + malware)
    // IPv4
    const val DNS_PRIMARY = "1.1.1.3"
    const val DNS_SECONDARY = "1.0.0.3"
    // IPv6
    const val DNS_PRIMARY_V6 = "2606:4700:4700::1113"
    const val DNS_SECONDARY_V6 = "2606:4700:4700::1003"

    // Alternative: Cloudflare standard DNS (if family mode is not needed)
    const val DNS_STANDARD_PRIMARY = "1.1.1.1"
    const val DNS_STANDARD_SECONDARY = "1.0.0.1"

    // VPN session name
    const val SESSION_NAME = "AlNoor Content Guard"

    // Notification channel
    const val NOTIFICATION_CHANNEL_ID = "vpn_service_channel"
    const val NOTIFICATION_CHANNEL_NAME = "VPN Service"
    const val NOTIFICATION_ID = 1001
}
