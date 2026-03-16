package com.exampro.app.presentation.screens.subject

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.SubjectCard
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.viewmodels.SubjectUiState
import com.exampro.app.presentation.viewmodels.SubjectViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SubjectListScreen(
    examId: Int,
    onSubjectClick: (Int, String?) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: (() -> Unit)? = null,
    viewModel: SubjectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val purpose by viewModel.purpose.collectAsState()

    LaunchedEffect(examId) {
        viewModel.setExamId(examId)
    }

    val baseTitle = if (purpose == "questions") "Select Subject" else "Subjects"
    val title = when (val state = uiState) {
        is SubjectUiState.Success -> state.examName ?: baseTitle
        else -> baseTitle
    }

    SubjectListContent(
        uiState = uiState,
        title = title,
        onSubjectClick = { subjectId -> onSubjectClick(subjectId, purpose) },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onRefresh = { /* Sync happens at startup */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectListContent(
    uiState: SubjectUiState,
    title: String,
    onSubjectClick: (Int) -> Unit,
    onBackClick: () -> Unit,
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
                is SubjectUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is SubjectUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = onRefresh
                    )
                }
                is SubjectUiState.Success -> {
                    if (state.subjects.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No subjects available for this exam",
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
                                items = state.subjects,
                                key = { it.id }
                            ) { subject ->
                                SubjectCard(
                                    subject = subject,
                                    onClick = { onSubjectClick(subject.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
