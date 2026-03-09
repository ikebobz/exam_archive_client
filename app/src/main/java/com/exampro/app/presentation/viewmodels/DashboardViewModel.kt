package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.api.DashboardApi
import com.exampro.app.data.models.DashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val stats: DashboardStats) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val response = dashboardApi.getStats()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = DashboardUiState.Success(response.body()!!)
                } else {
                    _uiState.value = DashboardUiState.Error("Failed to load stats: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Failed to load dashboard stats")
            }
        }
    }

    fun refresh() {
        loadStats()
    }
}
