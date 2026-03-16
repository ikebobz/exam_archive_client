package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Question
import com.exampro.app.data.repository.ExamRepository
import com.exampro.app.data.repository.QuestionRepository
import com.exampro.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuestionDetailUiState {
    object Loading : QuestionDetailUiState()
    data class Success(
        val question: Question,
        val hasNext: Boolean,
        val hasPrevious: Boolean,
        val examName: String? = null,
        val subjectName: String? = null
    ) : QuestionDetailUiState()
    data class Error(val message: String) : QuestionDetailUiState()
}

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val examRepository: ExamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var questionId: Int = savedStateHandle.get<Int>("questionId") ?: -1
    private var allQuestionsInSubject: List<Question> = emptyList()

    private val _uiState = MutableStateFlow<QuestionDetailUiState>(QuestionDetailUiState.Loading)
    val uiState: StateFlow<QuestionDetailUiState> = _uiState.asStateFlow()

    private var currentExamName: String? = null
    private var currentSubjectName: String? = null

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
                        if (currentSubjectName == null) {
                            loadMetadata(question.subjectId)
                        }
                        
                        if (allQuestionsInSubject.isEmpty()) {
                            questionRepository.getQuestionsBySubjectFlow(question.subjectId).first().let { questions ->
                                allQuestionsInSubject = questions
                                updateUiState(question)
                            }
                        } else {
                            updateUiState(question)
                        }
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

    private fun loadMetadata(subjectId: Int) {
        viewModelScope.launch {
            subjectRepository.getSubject(subjectId).onSuccess { subject ->
                currentSubjectName = subject.name
                examRepository.getExam(subject.examId).onSuccess { exam ->
                    currentExamName = exam.name
                    updateMetadataInSuccessState()
                }
            }
        }
    }

    private fun updateMetadataInSuccessState() {
        val currentState = _uiState.value
        if (currentState is QuestionDetailUiState.Success) {
            _uiState.value = currentState.copy(
                examName = currentExamName,
                subjectName = currentSubjectName
            )
        }
    }

    private fun updateUiState(question: Question) {
        val currentIndex = allQuestionsInSubject.indexOfFirst { it.id == question.id }
        _uiState.value = QuestionDetailUiState.Success(
            question = question,
            hasNext = currentIndex != -1 && currentIndex < allQuestionsInSubject.size - 1,
            hasPrevious = currentIndex > 0,
            examName = currentExamName,
            subjectName = currentSubjectName
        )
    }

    fun toggleBookmark() {
        val state = _uiState.value
        if (state is QuestionDetailUiState.Success) {
            viewModelScope.launch {
                val newStatus = questionRepository.toggleBookmark(state.question)
                _uiState.value = state.copy(question = state.question.copy(isBookmarked = newStatus))
                
                // Also update the status in our list to keep navigation consistent
                allQuestionsInSubject = allQuestionsInSubject.map {
                    if (it.id == state.question.id) it.copy(isBookmarked = newStatus) else it
                }
            }
        }
    }

    fun navigateToNext() {
        val currentIndex = allQuestionsInSubject.indexOfFirst { it.id == questionId }
        if (currentIndex != -1 && currentIndex < allQuestionsInSubject.size - 1) {
            questionId = allQuestionsInSubject[currentIndex + 1].id
            loadQuestion()
        }
    }

    fun navigateToPrevious() {
        val currentIndex = allQuestionsInSubject.indexOfFirst { it.id == questionId }
        if (currentIndex > 0) {
            questionId = allQuestionsInSubject[currentIndex - 1].id
            loadQuestion()
        }
    }

    fun refresh() {
        loadQuestion()
    }
}
