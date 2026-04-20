package com.exampro.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.models.DashboardStats
import com.exampro.app.data.repository.AuthRepository
import com.exampro.app.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val stats: DashboardStats) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val examDao: ExamDao,
    private val subjectDao: SubjectDao,
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        examDao.getAllExams(),
        subjectDao.getAllSubjects(),
        questionRepository.getBookmarkedQuestionsFlow()
    ) { exams, subjects, bookmarkedQuestions ->
        val stats = DashboardStats(
            totalExams = exams.size,
            totalSubjects = subjects.size,
            totalQuestions = bookmarkedQuestions.size
        )
        authRepository.saveStats(stats)
        DashboardUiState.Success(stats)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Success(authRepository.getCachedStats())
    )
}
