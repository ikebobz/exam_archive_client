package com.exampro.app.presentation.screens.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.exampro.app.presentation.components.FilterChipItem
import com.exampro.app.presentation.components.QuestionCard
import com.exampro.app.presentation.components.SearchBar
import com.exampro.app.presentation.viewmodels.QuestionUiState
import com.exampro.app.presentation.viewmodels.QuestionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionListScreen(
    subjectId: Int,
    onQuestionClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val availableYears by viewModel.availableYears.collectAsState()
    val availableDifficulties by viewModel.availableDifficulties.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(subjectId) {
        viewModel.setSubjectId(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Questions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.refresh()
                    delay(500)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is QuestionUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is QuestionUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
                is QuestionUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            placeholder = "Search questions..."
                        )

                        if (availableDifficulties.isNotEmpty() || availableYears.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                availableDifficulties.forEach { difficulty ->
                                    FilterChipItem(
                                        label = difficulty.replaceFirstChar { it.uppercase() },
                                        selected = selectedDifficulty == difficulty,
                                        onClick = {
                                            viewModel.setDifficultyFilter(
                                                if (selectedDifficulty == difficulty) null
                                                else difficulty
                                            )
                                        }
                                    )
                                }
                                availableYears.forEach { year ->
                                    FilterChipItem(
                                        label = "$year",
                                        selected = selectedYear == year,
                                        onClick = {
                                            viewModel.setYearFilter(
                                                if (selectedYear == year) null else year
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        if (state.questions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotEmpty() || selectedDifficulty != null || selectedYear != null)
                                        "No questions match your filters"
                                    else
                                        "No questions available",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(
                                    items = state.questions,
                                    key = { it.id }
                                ) { question ->
                                    QuestionCard(
                                        question = question,
                                        onClick = { onQuestionClick(question.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
