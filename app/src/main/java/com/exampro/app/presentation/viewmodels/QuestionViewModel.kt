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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuestionUiState {
    object Loading : QuestionUiState()
    data class Success(val questions: List<Question>) : QuestionUiState()
    data class Error(val message: String) : QuestionUiState()
}

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionUiState>(QuestionUiState.Loading)
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())

    private val _selectedSubjectId = MutableStateFlow<Int?>(savedStateHandle.get<Int>("subjectId"))
    val selectedSubjectId: StateFlow<Int?> = _selectedSubjectId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()

    private val _selectedYear = MutableStateFlow<Int?>(null)
    val selectedYear: StateFlow<Int?> = _selectedYear.asStateFlow()

    private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
    val availableYears: StateFlow<List<Int>> = _availableYears.asStateFlow()

    private val _availableDifficulties = MutableStateFlow<List<String>>(emptyList())
    val availableDifficulties: StateFlow<List<String>> = _availableDifficulties.asStateFlow()

    init {
        loadQuestions()
        observeFilters()
    }

    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                _allQuestions,
                _searchQuery,
                _selectedDifficulty,
                _selectedYear
            ) { questions, query, difficulty, year ->
                applyFilters(questions, query, difficulty, year)
            }.collect { filtered ->
                if (_uiState.value !is QuestionUiState.Loading) {
                    _uiState.value = QuestionUiState.Success(filtered)
                }
            }
        }
    }

    private fun applyFilters(
        questions: List<Question>,
        query: String,
        difficulty: String?,
        year: Int?
    ): List<Question> {
        return questions.filter { question ->
            val matchesSearch = query.isBlank() ||
                question.questionText.contains(query, ignoreCase = true) ||
                (question.topic?.contains(query, ignoreCase = true) == true)

            val matchesDifficulty = difficulty == null ||
                question.difficulty.equals(difficulty, ignoreCase = true)

            val matchesYear = year == null || question.year == year

            matchesSearch && matchesDifficulty && matchesYear
        }
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = QuestionUiState.Loading
            val subjectId = _selectedSubjectId.value
            val result = if (subjectId != null) {
                questionRepository.getQuestionsBySubject(subjectId)
            } else {
                questionRepository.refreshQuestions()
            }
            result.onSuccess { questions ->
                _allQuestions.value = questions
                _availableYears.value = questions
                    .mapNotNull { it.year }
                    .distinct()
                    .sorted()
                _availableDifficulties.value = questions
                    .mapNotNull { it.difficulty }
                    .distinct()
                    .sorted()
                _uiState.value = QuestionUiState.Success(
                    applyFilters(questions, _searchQuery.value, _selectedDifficulty.value, _selectedYear.value)
                )
            }.onFailure { e ->
                _uiState.value = QuestionUiState.Error(e.message ?: "Failed to load questions")
            }
        }
    }

    fun setSubjectId(subjectId: Int) {
        _selectedSubjectId.value = subjectId
        loadQuestions()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDifficultyFilter(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }

    fun setYearFilter(year: Int?) {
        _selectedYear.value = year
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedDifficulty.value = null
        _selectedYear.value = null
    }

    fun refresh() {
        loadQuestions()
    }
}
