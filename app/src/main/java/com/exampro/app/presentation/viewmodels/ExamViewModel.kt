package com.exampro.app.presentation.viewmodels

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
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamUiState>(ExamUiState.Loading)
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    init {
        loadExams()
    }

    fun loadExams() {
        viewModelScope.launch {
            _uiState.value = ExamUiState.Loading
            val result = examRepository.refreshExams()
            result.onSuccess { exams ->
                _uiState.value = ExamUiState.Success(exams)
            }.onFailure { e ->
                _uiState.value = ExamUiState.Error(e.message ?: "Failed to load exams")
            }
        }
    }

    fun refresh() {
        loadExams()
    }
}
