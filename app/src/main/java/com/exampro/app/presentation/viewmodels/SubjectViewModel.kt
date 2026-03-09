package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Subject
import com.exampro.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubjectUiState {
    object Loading : SubjectUiState()
    data class Success(val subjects: List<Subject>) : SubjectUiState()
    data class Error(val message: String) : SubjectUiState()
}

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubjectUiState>(SubjectUiState.Loading)
    val uiState: StateFlow<SubjectUiState> = _uiState.asStateFlow()

    private val _selectedExamId = MutableStateFlow<Int?>(savedStateHandle.get<Int>("examId"))
    val selectedExamId: StateFlow<Int?> = _selectedExamId.asStateFlow()

    init {
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.value = SubjectUiState.Loading
            val examId = _selectedExamId.value
            val result = if (examId != null) {
                subjectRepository.getSubjectsByExam(examId)
            } else {
                subjectRepository.refreshSubjects()
            }
            result.onSuccess { subjects ->
                _uiState.value = SubjectUiState.Success(subjects)
            }.onFailure { e ->
                _uiState.value = SubjectUiState.Error(e.message ?: "Failed to load subjects")
            }
        }
    }

    fun setExamId(examId: Int) {
        _selectedExamId.value = examId
        loadSubjects()
    }

    fun refresh() {
        loadSubjects()
    }
}
