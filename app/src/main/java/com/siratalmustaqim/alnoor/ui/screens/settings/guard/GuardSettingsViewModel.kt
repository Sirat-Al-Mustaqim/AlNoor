package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardSettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val vpnEnabled: StateFlow<Boolean> = settingsDataStore.vpnEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleVpn(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateVpnEnabled(enabled)
        }
    }
}
