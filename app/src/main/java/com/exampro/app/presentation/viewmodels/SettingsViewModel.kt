package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.repository.SettingsRepository
import com.exampro.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val baseUrl: String = Constants.BASE_URL,
    val maxPrefetch: Int = Constants.DEFAULT_MAX_PREFETCH_QUESTIONS,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val baseUrl: StateFlow<String> = settingsRepository.baseUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.BASE_URL)

    val maxPrefetch: StateFlow<Int> = settingsRepository.maxPrefetch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_MAX_PREFETCH_QUESTIONS)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateSettings(newUrl: String, newMaxPrefetch: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)
            try {
                if (newUrl.isBlank()) {
                    settingsRepository.resetBaseUrl()
                } else {
                    settingsRepository.updateBaseUrl(newUrl)
                }
                
                val prefetchValue = newMaxPrefetch.toIntOrNull() ?: Constants.DEFAULT_MAX_PREFETCH_QUESTIONS
                settingsRepository.updateMaxPrefetch(prefetchValue)

                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "Failed to save settings")
            }
        }
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, error = null)
    }
}
