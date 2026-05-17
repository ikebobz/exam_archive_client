package com.exampro.app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.api.DeviceApi
import com.exampro.app.data.api.DeviceRegistrationRequest
import com.exampro.app.data.repository.SyncRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val deviceApi: DeviceApi
) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState = _syncState.asStateFlow()

    fun performInitialSync() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            
            // 1. Sync Data
            val result = syncRepository.syncAllData()
            
            // 2. Register Device Token (Background)
            syncDeviceToken()

            result.onSuccess {
                _syncState.value = SyncState.Success
            }.onFailure { e ->
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }

    private fun syncDeviceToken() {
        viewModelScope.launch {
            try {
                Log.d("FCM_SYNC", "Fetching FCM token...")
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("FCM_SYNC", "Token retrieved: $token")
                
                val response = deviceApi.registerDevice(DeviceRegistrationRequest(token = token))
                if (response.isSuccessful) {
                    Log.d("FCM_SYNC", "Successfully registered device token on CMS")
                } else {
                    Log.e("FCM_SYNC", "Failed to register token: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("FCM_SYNC", "Error during device registration", e)
            }
        }
    }
}
