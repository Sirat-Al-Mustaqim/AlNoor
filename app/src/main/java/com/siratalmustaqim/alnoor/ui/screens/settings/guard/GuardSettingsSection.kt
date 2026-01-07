package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.siratalmustaqim.alnoor.ui.components.SettingsRowToggle
import com.siratalmustaqim.alnoor.ui.components.SettingsSection

@Composable
fun GuardSettingsSection(
    modifier: Modifier = Modifier,
    viewModel: GuardSettingsViewModel = hiltViewModel()
) {
    val vpnEnabled by viewModel.vpnEnabled.collectAsState()

    SettingsSection(
        title = "Guard",
        icon = "🛡️",
        modifier = modifier
    ) {
        SettingsRowToggle(
            label = "Always-On VPN",
            checked = vpnEnabled,
            onCheckedChange = { viewModel.toggleVpn(it) },
            subtitle = "Keep VPN active at all times for protection"
        )
    }
}
