package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.UserProfile
import com.exampro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Authenticated(val user: UserProfile) : AuthUiState()
    object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.getProfile()
            result.onSuccess { profile ->
                _isLoggedIn.value = true
                _uiState.value = AuthUiState.Authenticated(profile)
            }.onFailure {
                _isLoggedIn.value = false
                _uiState.value = AuthUiState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess {
                _isLoggedIn.value = true
                checkSession()
            }.onFailure { e ->
                _isLoggedIn.value = false
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, firstName: String?, lastName: String?) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(email, password, firstName, lastName)
            result.onSuccess {
                _isLoggedIn.value = true
                checkSession()
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.logout()
            _isLoggedIn.value = false
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
