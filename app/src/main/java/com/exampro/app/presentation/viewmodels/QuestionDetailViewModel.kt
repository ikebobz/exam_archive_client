package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Question
import com.exampro.app.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuestionDetailUiState {
    object Loading : QuestionDetailUiState()
    data class Success(val question: Question) : QuestionDetailUiState()
    data class Error(val message: String) : QuestionDetailUiState()
}

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val questionId: Int = savedStateHandle.get<Int>("questionId") ?: -1

    private val _uiState = MutableStateFlow<QuestionDetailUiState>(QuestionDetailUiState.Loading)
    val uiState: StateFlow<QuestionDetailUiState> = _uiState.asStateFlow()

    init {
        loadQuestion()
    }

    fun loadQuestion() {
        viewModelScope.launch {
            _uiState.value = QuestionDetailUiState.Loading
            try {
                val result = questionRepository.getQuestion(questionId)
                result.fold(
                    onSuccess = { question ->
                        _uiState.value = QuestionDetailUiState.Success(question)
                    },
                    onFailure = { error ->
                        _uiState.value = QuestionDetailUiState.Error(error.message ?: "Question not found")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = QuestionDetailUiState.Error(e.message ?: "Failed to load question")
            }
        }
    }

    fun refresh() {
        loadQuestion()
    }
}
