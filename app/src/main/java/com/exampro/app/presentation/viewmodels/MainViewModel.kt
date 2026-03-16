package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState = _syncState.asStateFlow()

    fun performInitialSync() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            val result = syncRepository.syncAllData()
            result.onSuccess {
                _syncState.value = SyncState.Success
            }.onFailure { e ->
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }
}
