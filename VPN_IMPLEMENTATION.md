# AlNoor Content Guard VPN Implementation

## Overview

This implementation provides a local VPN-based content filtering system for the AlNoor Android application. The VPN service blocks adult content by routing all DNS queries through Cloudflare's family-safe DNS servers (1.1.1.3 and 1.0.0.3).

## Architecture

### Components

1. **ContentBlockerVpnService** (`vpn/ContentBlockerVpnService.kt`)
   - Extends Android's `VpnService`
   - Creates a local VPN tunnel on the device
   - Routes all network traffic through the tunnel
   - Configures DNS to use Cloudflare's family-safe DNS

2. **VpnManager** (`vpn/VpnManager.kt`)
   - Manages VPN service lifecycle (start/stop)
   - Handles VPN permission requests
   - Provides VPN state tracking

3. **PacketHandler** (`vpn/PacketHandler.kt`)
   - Processes network packets passing through the VPN tunnel
   - Logs DNS queries for monitoring
   - Tracks statistics (bytes in/out, packets processed)

4. **GuardRepository** (`data/repository/GuardRepository.kt`)
   - Repository layer between ViewModel and VPN service
   - Manages Guard settings persistence
   - Coordinates VPN operations

5. **VpnConfig** (`vpn/VpnConfig.kt`)
   - Configuration constants for VPN setup
   - DNS server addresses
   - Network interface parameters

## How It Works

### Local VPN Tunnel

The implementation creates a **local VPN tunnel** on the Android device. This is different from a traditional VPN that routes traffic to a remote server:

1. A virtual network interface (TUN) is created on the device
2. All network traffic is routed through this interface
3. The app processes packets locally and forwards them to the real network
4. DNS queries are intercepted and resolved using Cloudflare's family-safe DNS

### Content Filtering

Content filtering is achieved through **DNS-based blocking**:

- **Cloudflare Family DNS (1.1.1.3)** automatically blocks:
  - Adult content websites
  - Malware domains
  - Phishing sites
  
- The VPN service configures the system to use these DNS servers
- When a user tries to access blocked content, the DNS resolution fails
- No remote server or external service is involved - all processing is local

### WireGuard-Style Approach

While this implementation doesn't use the WireGuard protocol itself (which would require native code and kernel modules), it follows WireGuard's philosophy:

- **Simple and minimal**: Clean, focused codebase
- **Secure by default**: Encrypted tunnel, no remote servers
- **High performance**: Local processing, minimal overhead
- **Modern Android APIs**: Uses Android's VpnService API

## Integration with UI

The VPN service integrates with the existing Guard settings:

- **Always-on VPN** toggle starts/stops the VPN service
- **Always-on Protection** ensures VPN stays active during prayer times
- **Offline Mode** can block all internet traffic (future enhancement)

The GuardSettingsViewModel coordinates between:
- User settings (DataStore)
- VPN service state
- UI state and events

## Permissions Required

The following permissions are declared in AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

The VPN service requires user consent through Android's VPN permission system.

## Security Considerations

1. **No data collection**: All processing is local, no data sent to external servers
2. **Open DNS**: Uses Cloudflare's public DNS (trusted, privacy-focused)
3. **Foreground service**: Users always know when VPN is active
4. **User control**: Users can disable VPN at any time

## Testing

To test the VPN functionality:

1. Enable "Always-on VPN" in Guard Settings
2. Grant VPN permission when prompted
3. Verify the notification appears showing VPN is active
4. Test DNS resolution with family-safe filtering
5. Try accessing adult content - should be blocked
6. Disable VPN and verify normal operation resumes

## Future Enhancements

Potential improvements:

1. **Enhanced packet filtering**: Block specific IPs/domains beyond DNS
2. **Offline mode implementation**: Full internet blocking when enabled
3. **Statistics dashboard**: Show blocked requests, data usage
4. **Custom block lists**: User-configurable domain blocking
5. **Scheduled protection**: Auto-enable during prayer times
6. **Split tunneling**: Exclude specific apps from VPN

## Code Structure

```
com.siratalmustaqim.alnoor/
├── vpn/
│   ├── ContentBlockerVpnService.kt   # Main VPN service
│   ├── VpnManager.kt                  # VPN lifecycle manager
│   ├── VpnConfig.kt                   # Configuration constants
│   ├── VpnState.kt                    # State definitions
│   └── PacketHandler.kt               # Packet processing
├── data/
│   ├── repository/
│   │   └── GuardRepository.kt         # Repository layer
│   └── preferences/
│       └── SettingsDataStore.kt       # Settings persistence
├── di/
│   └── VpnModule.kt                   # Dependency injection
└── ui/screens/settings/guard/
    └── GuardSettingsViewModel.kt      # UI state management
```

## Dependencies

No additional dependencies required beyond what's already in the project:
- Android VpnService API (part of Android SDK)
- Kotlin Coroutines (already included)
- Hilt (already included)
- Timber (already included for logging)

## Notes

- The implementation is fully local and doesn't require any backend server
- DNS-based filtering is effective for most adult content sites
- Some sophisticated sites may bypass DNS filtering (would need additional packet inspection)
- VPN service runs as a foreground service to ensure reliability
- Battery impact is minimal as processing is lightweight
