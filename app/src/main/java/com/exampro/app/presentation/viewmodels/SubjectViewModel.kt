package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Subject
import com.exampro.app.data.repository.ExamRepository
import com.exampro.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubjectUiState {
    object Loading : SubjectUiState()
    data class Success(
        val subjects: List<Subject>,
        val examName: String? = null
    ) : SubjectUiState()
    data class Error(val message: String) : SubjectUiState()
}

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val examRepository: ExamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedExamId = MutableStateFlow<Int?>(savedStateHandle.get<Int>("examId"))
    val selectedExamId: StateFlow<Int?> = _selectedExamId.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _examName = MutableStateFlow<String?>(null)

    private val _purpose = MutableStateFlow(savedStateHandle.get<String>("purpose"))
    val purpose: StateFlow<String?> = _purpose.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SubjectUiState> = _selectedExamId
        .flatMapLatest { examId ->
            if (examId != null && examId != 0) {
                subjectRepository.getSubjectsByExamFlow(examId)
            } else {
                subjectRepository.getSubjectsFlow()
            }
        }
        .combine(_examName) { subjects, examName -> subjects to examName }
        .combine(_isSyncing) { (subjects, examName), syncing -> Triple(subjects, examName, syncing) }
        .combine(_errorMessage) { (subjects, examName, syncing), error ->
            if (error != null && subjects.isEmpty() && !syncing) {
                SubjectUiState.Error(error)
            } else if (subjects.isEmpty() && syncing) {
                SubjectUiState.Loading
            } else {
                SubjectUiState.Success(subjects, examName)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SubjectUiState.Loading
        )

    init {
        loadExamName()
        refresh()
    }

    private fun loadExamName() {
        val examId = _selectedExamId.value
        if (examId != null && examId != 0) {
            viewModelScope.launch {
                examRepository.getExam(examId).onSuccess {
                    _examName.value = it.name
                }
            }
        }
    }

    fun setExamId(examId: Int) {
        if (_selectedExamId.value != examId) {
            _selectedExamId.value = examId
            _errorMessage.value = null
            loadExamName()
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isSyncing.value = true
            
            val examId = _selectedExamId.value
            val result = if (examId != null && examId != 0) {
                subjectRepository.getSubjectsByExam(examId)
            } else {
                subjectRepository.refreshSubjects()
            }
            
            result.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to load subjects"
            }.onSuccess {
                _errorMessage.value = null
            }
            _isSyncing.value = false
        }
    }
}
