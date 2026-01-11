package com.siratalmustaqim.alnoor.ui.screens.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import com.siratalmustaqim.alnoor.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AyahVerificationUiState(
    val enteredAyahs: List<String> = emptyList(),
    val currentInput: String = "",
    val isValidating: Boolean = false,
    val onInputChange: (String) -> Unit = {},
    val onAddAyah: () -> Unit = {},
    val onClearAll: () -> Unit = {},
    val onValidate: () -> Unit = {}
) {
    val canValidate: Boolean = enteredAyahs.size == 13
    val remainingCount: Int = 13 - enteredAyahs.size
}

sealed class AyahVerificationEvent {
    data class ShowToast(val message: String) : AyahVerificationEvent()
    data object VerificationSuccess : AyahVerificationEvent()
    data object VerificationFailed : AyahVerificationEvent()
}

@HiltViewModel
class AyahVerificationViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    private val guardRepository: GuardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AyahVerificationUiState(
            onInputChange = ::updateInput,
            onAddAyah = ::addAyah,
            onClearAll = ::clearAll,
            onValidate = ::validate
        )
    )
    val uiState: StateFlow<AyahVerificationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AyahVerificationEvent>()
    val events = _events.asSharedFlow()

    private fun updateInput(input: String) {
        _uiState.update { 
            it.copy(
                currentInput = input,
                onInputChange = ::updateInput,
                onAddAyah = ::addAyah,
                onClearAll = ::clearAll,
                onValidate = ::validate
            ) 
        }
    }

    private fun addAyah() {
        val ayahText = _uiState.value.currentInput.trim()
        
        if (ayahText.isBlank()) {
            viewModelScope.launch {
                _events.emit(AyahVerificationEvent.ShowToast("Please enter an ayah"))
            }
            return
        }

        // Check for duplicate
        if (_uiState.value.enteredAyahs.contains(ayahText)) {
            viewModelScope.launch {
                _events.emit(AyahVerificationEvent.ShowToast("This ayah is already added"))
            }
            return
        }

        // Check if already have 13
        if (_uiState.value.enteredAyahs.size >= 13) {
            viewModelScope.launch {
                _events.emit(AyahVerificationEvent.ShowToast("You have already added 13 ayahs"))
            }
            return
        }

        // Add to list
        _uiState.update {
            it.copy(
                enteredAyahs = it.enteredAyahs + ayahText,
                currentInput = "",
                onInputChange = ::updateInput,
                onAddAyah = ::addAyah,
                onClearAll = ::clearAll,
                onValidate = ::validate
            )
        }
    }

    private fun clearAll() {
        _uiState.update {
            it.copy(
                enteredAyahs = emptyList(),
                currentInput = "",
                onInputChange = ::updateInput,
                onAddAyah = ::addAyah,
                onClearAll = ::clearAll,
                onValidate = ::validate
            )
        }
    }

    private fun validate() {
        if (!_uiState.value.canValidate) return

        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isValidating = true,
                    onInputChange = ::updateInput,
                    onAddAyah = ::addAyah,
                    onClearAll = ::clearAll,
                    onValidate = ::validate
                ) 
            }

            try {
                var allValid = true
                
                for (ayahText in _uiState.value.enteredAyahs) {
                    val results = quranRepository.searchAyahExact(ayahText)
                    if (!results) {
                        allValid = false
                        break
                    }
                }

                if (allValid) {
                    guardRepository.setAlwaysOnProtection(false)
                    _events.emit(AyahVerificationEvent.VerificationSuccess)
                } else {
                    _events.emit(AyahVerificationEvent.ShowToast("One or more ayahs are incorrect"))
                    _events.emit(AyahVerificationEvent.VerificationFailed)
                }
            } catch (e: Exception) {
                _events.emit(AyahVerificationEvent.ShowToast("Error validating ayahs: ${e.message}"))
                _events.emit(AyahVerificationEvent.VerificationFailed)
            } finally {
                _uiState.update { 
                    it.copy(
                        isValidating = false,
                        onInputChange = ::updateInput,
                        onAddAyah = ::addAyah,
                        onClearAll = ::clearAll,
                        onValidate = ::validate
                    ) 
                }
            }
        }
    }
}
