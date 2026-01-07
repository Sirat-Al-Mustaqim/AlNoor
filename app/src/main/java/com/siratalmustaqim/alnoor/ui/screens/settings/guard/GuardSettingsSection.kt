package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.siratalmustaqim.alnoor.ui.components.SettingsRowToggle
import com.siratalmustaqim.alnoor.ui.components.SettingsSection
import com.siratalmustaqim.alnoor.ui.screens.settings.GuardSettingsUiState

@Composable
fun GuardSettingsSection(
    uiState: GuardSettingsUiState,
    onVpnToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsSection(
        title = "Guard",
        icon = "🛡️",
        modifier = modifier
    ) {
        SettingsRowToggle(
            label = "Always-On VPN",
            checked = uiState.vpnEnabled,
            onCheckedChange = onVpnToggle,
            subtitle = "Keep VPN active at all times for protection"
        )
    }
}
