package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Exam
import com.exampro.app.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _purpose = MutableStateFlow(savedStateHandle.get<String>("purpose"))
    val purpose: StateFlow<String?> = _purpose.asStateFlow()

    val uiState: StateFlow<ExamUiState> = examRepository.getExamsFlow()
        .map { ExamUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ExamUiState.Loading
        )
}
