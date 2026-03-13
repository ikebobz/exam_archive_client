package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Exam
import com.exampro.app.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExamUiState {
    object Loading : ExamUiState()
    data class Success(val exams: List<Exam>) : ExamUiState()
    data class Error(val message: String) : ExamUiState()
}

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamUiState>(ExamUiState.Loading)
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private val _purpose = MutableStateFlow(savedStateHandle.get<String>("purpose"))
    val purpose: StateFlow<String?> = _purpose.asStateFlow()

    init {
        observeExams()
        refresh()
    }

    private fun observeExams() {
        viewModelScope.launch {
            examRepository.getExamsFlow().collect { exams ->
                // If we have data, show it immediately and stop loading
                if (exams.isNotEmpty() || _uiState.value !is ExamUiState.Loading) {
                    _uiState.value = ExamUiState.Success(exams)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val result = examRepository.refreshExams()
            if (result.isFailure && _uiState.value is ExamUiState.Loading) {
                _uiState.value = ExamUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load exams")
            }
        }
    }
}
