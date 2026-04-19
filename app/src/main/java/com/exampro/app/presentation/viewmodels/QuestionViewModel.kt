package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Question
import com.exampro.app.data.repository.ExamRepository
import com.exampro.app.data.repository.QuestionRepository
import com.exampro.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _subjectId = MutableStateFlow<Int?>(savedStateHandle.get<Int>("subjectId"))
    val selectedSubjectId: StateFlow<Int?> = _subjectId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTopic = MutableStateFlow<String?>(null)
    val selectedTopic: StateFlow<String?> = _selectedTopic.asStateFlow()

    private val _examName = MutableStateFlow<String?>(null)
    private val _subjectName = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _allQuestions = _subjectId.flatMapLatest { id ->
        when {
            id == -1 -> {
                _subjectName.value = "Bookmarks"
                _examName.value = null
                questionRepository.getBookmarkedQuestionsFlow()
            }
            id != null && id != 0 -> {
                loadMetadata(id)
                questionRepository.getQuestionsBySubjectFlow(id)
            }
            else -> flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val availableTopics: StateFlow<List<String>> = _allQuestions
        .map { questions ->
            questions.mapNotNull { it.topic?.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .sortedBy { it.lowercase() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<QuestionUiState> = combine(
        _allQuestions,
        _searchQuery,
        _selectedTopic,
        _examName,
        _subjectName
    ) { questions, query, topic, examName, subjectName ->
        val filtered = questions.filter { question ->
            val matchesSearch = query.isBlank() ||
                question.questionText.contains(query, ignoreCase = true) ||
                (question.topic?.contains(query, ignoreCase = true) == true)

            val matchesTopic = topic == null ||
                question.topic?.trim()?.equals(topic.trim(), ignoreCase = true) == true

            matchesSearch && matchesTopic
        }
        println("Filtered questions are : $filtered")
        QuestionUiState.Success(
            questions = filtered,
            examName = examName,
            subjectName = subjectName,
            isBookmarksOnly = _subjectId.value == -1
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuestionUiState.Loading
    )

    private fun loadMetadata(subjectId: Int) {
        viewModelScope.launch {
            subjectRepository.getSubject(subjectId).onSuccess { subject ->
                _subjectName.value = subject.name
                examRepository.getExam(subject.examId).onSuccess { exam ->
                    _examName.value = exam.name
                }
            }
        }
    }

    fun setSubjectId(subjectId: Int) {
        if (_subjectId.value != subjectId) {
            _subjectId.value = subjectId
            _selectedTopic.value = null
            _searchQuery.value = ""
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTopicFilter(topic: String?) {
        val normalized = if (topic.isNullOrBlank()) null else topic.trim()
        _selectedTopic.value = normalized
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedTopic.value = null
    }

    fun refresh() {
        // Data is reactive from local DB
    }
}
