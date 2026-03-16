package com.exampro.app.presentation.screens.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exampro.app.data.models.Exam
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.ExamCard
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.theme.ExamProTheme
import com.exampro.app.presentation.viewmodels.ExamUiState
import com.exampro.app.presentation.viewmodels.ExamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExamListScreen(
    onExamClick: (Int, String?) -> Unit,
    onBackClick: (() -> Unit)? = null,
    onHomeClick: (() -> Unit)? = null,
    viewModel: ExamViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val purpose by viewModel.purpose.collectAsState()

    ExamListContent(
        uiState = uiState,
        title = if (purpose != null) "Select Exam" else "Exams",
        onExamClick = { examId -> onExamClick(examId, purpose) },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onRefresh = { /* No manual refresh in prefetch mode */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamListContent(
    uiState: ExamUiState,
    title: String,
    onExamClick: (Int) -> Unit,
    onBackClick: (() -> Unit)? = null,
    onHomeClick: (() -> Unit)? = null,
    onRefresh: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    onRefresh()
                    delay(500)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ExamUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ExamUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = onRefresh
                    )
                }
                is ExamUiState.Success -> {
                    if (state.exams.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No exams available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.exams,
                                key = { it.id }
                            ) { exam ->
                                ExamCard(
                                    exam = exam,
                                    onClick = { onExamClick(exam.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamListScreenPreview() {
    val sampleExams = listOf(
        Exam(
            id = 1,
            name = "Civil Engineering",
            code = "CE101",
            description = "Basic Civil Engineering concepts and practices.",
            createdAt = null,
            updatedAt = null
        ),
        Exam(
            id = 2,
            name = "Computer Science",
            code = "CS202",
            description = "Advanced algorithms and data structures.",
            createdAt = null,
            updatedAt = null
        ),
        Exam(
            id = 3,
            name = "Mechanical Engineering",
            code = "ME303",
            description = "Principles of thermodynamics and mechanics.",
            createdAt = null,
            updatedAt = null
        )
    )

    ExamProTheme {
        ExamListContent(
            uiState = ExamUiState.Success(sampleExams),
            title = "Exams",
            onExamClick = {},
            onBackClick = {},
            onHomeClick = {},
            onRefresh = {}
        )
    }
}
