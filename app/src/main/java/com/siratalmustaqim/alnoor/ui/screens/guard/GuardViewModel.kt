package com.siratalmustaqim.alnoor.ui.screens.guard

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.R
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class GuardUiState(
    val protectionEnabled: Boolean = false,
    val alwaysOnProtection: Boolean = false,
    val isDeviceOwner: Boolean = false,
    val onToggleProtection: () -> Unit = {},
    val onAlwaysOnProtectionChange: (Boolean) -> Unit = {}
) {
    @get:StringRes
    val statusTextRes: Int
        get() = if (protectionEnabled) R.string.guard_status_protected else R.string.guard_status_protection_off
}

sealed class GuardEvent {
    data class ShowError(@param:StringRes val messageRes: Int) : GuardEvent()
    data class ShowMessage(@param:StringRes val messageRes: Int) : GuardEvent()
    data object NavigateToVerification : GuardEvent()
}

@HiltViewModel
class GuardViewModel @Inject constructor(
    private val guardRepository: GuardRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<GuardEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<GuardUiState> = combine(
        guardRepository.protectionEnabled,
        guardRepository.alwaysOnProtection
    ) { protectionEnabled, alwaysOnProtection ->
        GuardUiState(
            protectionEnabled = protectionEnabled,
            alwaysOnProtection = alwaysOnProtection,
            isDeviceOwner = guardRepository.isDeviceOwner,
            onToggleProtection = ::toggleProtection,
            onAlwaysOnProtectionChange = ::setAlwaysOnProtection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuardUiState(
            isDeviceOwner = guardRepository.isDeviceOwner,
            onToggleProtection = ::toggleProtection,
            onAlwaysOnProtectionChange = ::setAlwaysOnProtection
        )
    )

    private fun toggleProtection() {
        viewModelScope.launch {
            val alwaysOnEnabled = uiState.value.alwaysOnProtection
            val currentlyProtected = uiState.value.protectionEnabled

            Timber.d("Toggle protection: currentlyProtected=$currentlyProtected, alwaysOn=$alwaysOnEnabled")

            // Check if trying to disable while always-on is enabled
            if (currentlyProtected && alwaysOnEnabled) {
                Timber.d("Cannot disable protection: always-on protection is enabled")
                _events.emit(GuardEvent.ShowError(R.string.guard_cannot_disable_always_on))
                return@launch
            }

            // Check if device owner
            if (!guardRepository.isDeviceOwner) {
                Timber.d("Cannot toggle protection: not device owner")
                _events.emit(GuardEvent.ShowError(R.string.guard_device_owner_required))
                return@launch
            }

            val success = guardRepository.toggleProtection()
            if (success) {
                _events.emit(GuardEvent.ShowMessage(R.string.guard_protection_enabled))
            } else if (!uiState.value.protectionEnabled) {
                _events.emit(GuardEvent.ShowMessage(R.string.guard_protection_disabled))
            }
        }
    }

    private fun setAlwaysOnProtection(enabled: Boolean) {
        viewModelScope.launch {
            Timber.d("Setting always-on protection: $enabled")
            
            // If trying to disable always-on protection, require verification
            if (!enabled && uiState.value.alwaysOnProtection) {
                Timber.d("Disabling always-on protection requires verification")
                _events.emit(GuardEvent.NavigateToVerification)
                return@launch
            }
            
            // Enable directly
            guardRepository.setAlwaysOnProtection(enabled)
        }
    }
}
