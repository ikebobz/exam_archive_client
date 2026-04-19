package com.exampro.app.presentation.screens.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.QuestionCard
import com.exampro.app.presentation.components.SearchBar
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.viewmodels.QuestionUiState
import com.exampro.app.presentation.viewmodels.QuestionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionListScreen(
    subjectId: Int,
    onQuestionClick: (Int, String?) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: (() -> Unit)? = null,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val availableTopics by viewModel.availableTopics.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(subjectId) {
        viewModel.setSubjectId(subjectId)
    }

    val title = when (val state = uiState) {
        is QuestionUiState.Success -> state.subjectName ?: "Questions"
        else -> "Questions"
    }

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
                        if (state.examName != null) {
                            Text(
                                text = state.examName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            placeholder = "Search questions..."
                        )

                        if (availableTopics.isNotEmpty()) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedTopic ?: "All Topics",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Filter by Topic") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Topics") },
                                        onClick = {
                                            viewModel.setTopicFilter(null)
                                            expanded = false
                                        }
                                    )
                                    availableTopics.forEach { topic ->
                                        DropdownMenuItem(
                                            text = { Text(topic) },
                                            onClick = {
                                                viewModel.setTopicFilter(topic)
                                                expanded = false
                                            }
                                        )
                                    }
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
                                    text = if (searchQuery.isNotEmpty() || selectedTopic != null)
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
                                        onClick = { onQuestionClick(question.id, selectedTopic) }
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
