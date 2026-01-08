package com.siratalmustaqim.alnoor.ui.screens.guard

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme
import com.siratalmustaqim.alnoor.ui.theme.Gold
import com.siratalmustaqim.alnoor.vpn.VpnState
import com.siratalmustaqim.alnoor.vpn.VpnStatistics

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun GuardScreen(
    viewModel: GuardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onVpnPermissionResult(result.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GuardEvent.RequestVpnPermission -> {
                    vpnPermissionLauncher.launch(event.intent)
                }

                is GuardEvent.ShowError -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }

                is GuardEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
            }
        }
    }

    GuardScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun GuardScreenContent(
    uiState: GuardUiState,
    snackbarHostState: SnackbarHostState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Hero Shield Section
            ShieldButton(
                isConnected = uiState.isConnected,
                isConnecting = uiState.isConnecting,
                onClick = uiState.onToggleVpn
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Text
            StatusSection(uiState = uiState)

            Spacer(modifier = Modifier.height(40.dp))

            // Statistics Card
            if (uiState.isConnected) {
                StatisticsCard(statistics = uiState.statistics)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Always-on VPN Toggle
            AlwaysOnVpnCard(
                enabled = uiState.alwaysOnVpn,
                onCheckedChange = uiState.onAlwaysOnVpnChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            InfoCard()

            Spacer(modifier = Modifier.height(80.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ShieldButton(
    isConnected: Boolean,
    isConnecting: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isConnected) 1.05f else 1f,
        animationSpec = tween(500),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(500),
        label = "backgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isConnected) Gold else MaterialTheme.colorScheme.outline,
        animationSpec = tween(500),
        label = "borderColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isConnected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(500),
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow for connected state
        if (isConnected) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                Gold.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Decorative ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .border(
                    width = if (isConnected) 3.dp else 1.dp,
                    color = borderColor.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )

        // Main button
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(2.dp, borderColor, CircleShape)
                .clickable(enabled = !isConnecting) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            } else {
                Icon(
                    imageVector = if (isConnected) Icons.Filled.Shield else Icons.Filled.Power,
                    contentDescription = if (isConnected) stringResource(R.string.guard_connected_desc) else stringResource(
                        R.string.guard_disconnected_desc
                    ),
                    tint = iconColor,
                    modifier = Modifier.size(72.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusSection(uiState: GuardUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(uiState.statusTextRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (uiState.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (uiState.isConnected) stringResource(R.string.guard_connection_secure) else stringResource(
                R.string.guard_tap_to_connect
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatisticsCard(statistics: VpnStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = stringResource(R.string.guard_stat_duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDuration(seconds = statistics.connectionTime),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}



@Composable
private fun AlwaysOnVpnCard(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.guard_always_on_vpn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.guard_auto_connect),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.guard_info_message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        hours > 0 -> stringResource(R.string.guard_format_hours_minutes, hours, minutes)
        minutes > 0 -> stringResource(R.string.guard_format_minutes_seconds, minutes, secs)
        else -> stringResource(R.string.guard_format_seconds, secs)
    }
}



@Preview(showBackground = true)
@Composable
private fun GuardScreenDisconnectedPreview() {
    AlNoorTheme {
        GuardScreenContent(
            uiState = GuardUiState(),
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuardScreenConnectedPreview() {
    AlNoorTheme {
        GuardScreenContent(
            uiState = GuardUiState(
                vpnState = VpnState.CONNECTED,
                alwaysOnVpn = true,
                statistics = VpnStatistics(
                    bytesIn = 15_000_000,
                    bytesOut = 2_500_000,
                    packetsBlocked = 42,
                    connectionTime = 3665
                )
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GuardScreenDarkPreview() {
    AlNoorTheme(darkTheme = true) {
        GuardScreenContent(
            uiState = GuardUiState(vpnState = VpnState.CONNECTED),
            snackbarHostState = SnackbarHostState()
        )
    }
}

