package com.siratalmustaqim.alnoor.ui.screens.settings.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class GuardSettingsUiState(
    val alwaysOnProtection: Boolean = false,
    val onAlwaysOnProtectionToggle: (Boolean) -> Unit = {}
)

@HiltViewModel
class GuardSettingsViewModel @Inject constructor(
    private val guardRepository: GuardRepository
) : ViewModel() {

    val uiState: StateFlow<GuardSettingsUiState> = guardRepository.alwaysOnProtection.map { alwaysOnProtection ->
        GuardSettingsUiState(
            alwaysOnProtection = alwaysOnProtection,
            onAlwaysOnProtectionToggle = ::toggleAlwaysOnProtection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuardSettingsUiState(
            onAlwaysOnProtectionToggle = ::toggleAlwaysOnProtection
        )
    )

    private fun toggleAlwaysOnProtection(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Toggling always-on protection: $enabled")
            guardRepository.setAlwaysOnProtection(enabled)
        }
    }
}
