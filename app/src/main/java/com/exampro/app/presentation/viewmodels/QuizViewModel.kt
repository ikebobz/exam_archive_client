package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.models.Answer
import com.exampro.app.data.models.Question
import com.exampro.app.data.repository.QuestionRepository
import com.exampro.app.data.repository.StudyProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val question: Question,
    val answers: List<Answer>
)

data class QuizAnswerResult(
    val questionIndex: Int,
    val selectedAnswerId: Int,
    val correctAnswerId: Int,
    val isCorrect: Boolean
)

sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Ready(val totalQuestions: Int) : QuizUiState()
    data class InProgress(
        val currentQuestion: QuizQuestion,
        val currentIndex: Int,
        val totalQuestions: Int,
        val score: Int,
        val selectedAnswerId: Int?,
        val isAnswered: Boolean,
        val elapsedSeconds: Int
    ) : QuizUiState()
    data class Finished(
        val score: Int,
        val totalQuestions: Int,
        val results: List<QuizAnswerResult>,
        val elapsedSeconds: Int
    ) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val studyProgressRepository: StudyProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var quizQuestions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private var results = mutableListOf<QuizAnswerResult>()
    private var elapsedSeconds = 0
    private var timerJob: Job? = null
    private var subjectId: Int? = null

    fun loadQuiz(subjectId: Int, questionCount: Int = 10) {
        this.subjectId = subjectId
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val questions = questionRepository.getQuestionsBySubjectFlow(subjectId).first()
                val shuffled = questions
                    .filter { !it.answers.isNullOrEmpty() }
                    .shuffled()
                    .take(questionCount)

                if (shuffled.isEmpty()) {
                    _uiState.value = QuizUiState.Error("No questions available for this subject")
                    return@launch
                }

                quizQuestions = shuffled.map { question ->
                    QuizQuestion(
                        question = question,
                        answers = (question.answers ?: emptyList()).shuffled()
                    )
                }

                currentIndex = 0
                score = 0
                results.clear()
                elapsedSeconds = 0

                _uiState.value = QuizUiState.Ready(totalQuestions = quizQuestions.size)
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Failed to load quiz")
            }
        }
    }

    fun startQuiz() {
        if (quizQuestions.isEmpty()) return
        startTimer()
        showCurrentQuestion()
    }

    fun selectAnswer(answerId: Int) {
        val state = _uiState.value
        if (state !is QuizUiState.InProgress || state.isAnswered) return

        val currentQuestion = quizQuestions[currentIndex]
        val correctAnswer = currentQuestion.answers.find { it.isCorrect }
        val isCorrect = correctAnswer?.id == answerId

        if (isCorrect) score++

        val answerResult = QuizAnswerResult(
            questionIndex = currentIndex,
            selectedAnswerId = answerId,
            correctAnswerId = correctAnswer?.id ?: -1,
            isCorrect = isCorrect
        )
        results.add(answerResult)

        _uiState.value = state.copy(
            score = score,
            selectedAnswerId = answerId,
            isAnswered = true
        )
    }

    fun nextQuestion() {
        currentIndex++
        if (currentIndex >= quizQuestions.size) {
            finishQuiz()
        } else {
            showCurrentQuestion()
        }
    }

    private fun showCurrentQuestion() {
        val question = quizQuestions[currentIndex]
        _uiState.value = QuizUiState.InProgress(
            currentQuestion = question,
            currentIndex = currentIndex,
            totalQuestions = quizQuestions.size,
            score = score,
            selectedAnswerId = null,
            isAnswered = false,
            elapsedSeconds = elapsedSeconds
        )
    }

    private fun finishQuiz() {
        stopTimer()
        _uiState.value = QuizUiState.Finished(
            score = score,
            totalQuestions = quizQuestions.size,
            results = results.toList(),
            elapsedSeconds = elapsedSeconds
        )
        saveProgress()
    }

    private fun saveProgress() {
        val sid = subjectId ?: return
        viewModelScope.launch {
            try {
                studyProgressRepository.saveQuizResult(
                    subjectId = sid,
                    totalQuestions = quizQuestions.size,
                    correctAnswers = score
                )
            } catch (_: Exception) {
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                val state = _uiState.value
                if (state is QuizUiState.InProgress) {
                    _uiState.value = state.copy(elapsedSeconds = elapsedSeconds)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun getQuizQuestion(index: Int): QuizQuestion? {
        return quizQuestions.getOrNull(index)
    }

    fun resetQuiz() {
        stopTimer()
        currentIndex = 0
        score = 0
        results.clear()
        elapsedSeconds = 0
        _uiState.value = if (quizQuestions.isNotEmpty()) {
            QuizUiState.Ready(totalQuestions = quizQuestions.size)
        } else {
            QuizUiState.Loading
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
