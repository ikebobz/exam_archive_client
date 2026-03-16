package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Question
import com.exampro.app.data.repository.ExamRepository
import com.exampro.app.data.repository.QuestionRepository
import com.exampro.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuestionUiState {
    object Loading : QuestionUiState()
    data class Success(
        val questions: List<Question>,
        val examName: String? = null,
        val subjectName: String? = null,
        val isBookmarksOnly: Boolean = false
    ) : QuestionUiState()
    data class Error(val message: String) : QuestionUiState()
}

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val examRepository: ExamRepository,
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

    private var currentExamName: String? = null
    private var currentSubjectName: String? = null
    private val isBookmarksOnly: Boolean
        get() = _selectedSubjectId.value == -1

    private var dataJob: Job? = null

    init {
        if (_selectedSubjectId.value != null) {
            initialize()
        }
        observeFilters()
    }

    private fun initialize() {
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            if (isBookmarksOnly) {
                currentSubjectName = "Bookmarks"
                currentExamName = null
                questionRepository.getBookmarkedQuestionsFlow().collect { questions ->
                    _allQuestions.value = questions
                    updateMetadataAndFilters(questions)
                }
            } else {
                loadMetadata()
                loadQuestions()
            }
        }
    }

    private fun loadMetadata() {
        val subjectId = _selectedSubjectId.value
        if (subjectId != null && subjectId != 0 && subjectId != -1) {
            viewModelScope.launch {
                subjectRepository.getSubject(subjectId).onSuccess { subject ->
                    currentSubjectName = subject.name
                    examRepository.getExam(subject.examId).onSuccess { exam ->
                        currentExamName = exam.name
                        updateSuccessStateIfReady()
                    }
                }
            }
        }
    }

    private fun updateSuccessStateIfReady() {
        val currentState = _uiState.value
        if (currentState is QuestionUiState.Success) {
            _uiState.value = currentState.copy(
                examName = currentExamName,
                subjectName = if (isBookmarksOnly) "Bookmarks" else currentSubjectName,
                isBookmarksOnly = isBookmarksOnly
            )
        }
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
                if (_uiState.value !is QuestionUiState.Loading || isBookmarksOnly || filtered.isNotEmpty()) {
                    _uiState.value = QuestionUiState.Success(
                        questions = filtered,
                        examName = currentExamName,
                        subjectName = if (isBookmarksOnly) "Bookmarks" else currentSubjectName,
                        isBookmarksOnly = isBookmarksOnly
                    )
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

    private fun updateMetadataAndFilters(questions: List<Question>) {
        _availableYears.value = questions
            .mapNotNull { it.year }
            .distinct()
            .sorted()
        _availableDifficulties.value = questions
            .mapNotNull { it.difficulty }
            .distinct()
            .sorted()
    }

    fun loadQuestions() {
        if (isBookmarksOnly) return
        
        viewModelScope.launch {
            _uiState.value = QuestionUiState.Loading
            val subjectId = _selectedSubjectId.value
            if (subjectId != null && subjectId != 0) {
                questionRepository.getQuestionsBySubjectFlow(subjectId).first().let { questions ->
                    _allQuestions.value = questions
                    updateMetadataAndFilters(questions)
                    _uiState.value = QuestionUiState.Success(
                        questions = applyFilters(questions, _searchQuery.value, _selectedDifficulty.value, _selectedYear.value),
                        examName = currentExamName,
                        subjectName = currentSubjectName,
                        isBookmarksOnly = false
                    )
                }
            } else {
                 _uiState.value = QuestionUiState.Error("No subject selected")
            }
        }
    }

    fun setSubjectId(subjectId: Int) {
        if (_selectedSubjectId.value != subjectId) {
            _selectedSubjectId.value = subjectId
            initialize()
        }
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
        if (!isBookmarksOnly) {
            loadQuestions()
        }
    }
}
