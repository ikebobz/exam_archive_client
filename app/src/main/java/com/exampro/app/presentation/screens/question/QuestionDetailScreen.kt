package com.exampro.app.presentation.screens.question

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.exampro.app.data.models.Answer
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.viewmodels.QuestionDetailUiState
import com.exampro.app.presentation.viewmodels.QuestionDetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionDetailScreen(
    questionId: Int,
    onBackClick: () -> Unit,
    onHomeClick: (() -> Unit)? = null,
    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExplanation by remember { mutableStateOf(false) }
    var selectedAnswerId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Question Detail",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                actions = {
                    if (uiState is QuestionDetailUiState.Success) {
                        val question = (uiState as QuestionDetailUiState.Success).question
                        IconButton(onClick = { viewModel.toggleBookmark() }) {
                            Icon(
                                imageVector = if (question.isBookmarked) Icons.Default.Bookmark 
                                             else Icons.Default.BookmarkBorder,
                                contentDescription = if (question.isBookmarked) "Remove bookmark" 
                                                     else "Bookmark question",
                                tint = if (question.isBookmarked) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is QuestionDetailUiState.Success) {
                val state = uiState as QuestionDetailUiState.Success
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { 
                            viewModel.navigateToPrevious()
                            showExplanation = false
                            selectedAnswerId = null
                        },
                        enabled = state.hasPrevious,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { 
                            viewModel.navigateToNext()
                            showExplanation = false
                            selectedAnswerId = null
                        },
                        enabled = state.hasNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is QuestionDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is QuestionDetailUiState.Error -> {
                Box(modifier = Modifier.padding(paddingValues)) {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
            is QuestionDetailUiState.Success -> {
                val question = state.question
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    if (state.examName != null || state.subjectName != null) {
                        Text(
                            text = listOfNotNull(state.examName, state.subjectName).joinToString(" > "),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        question.difficulty?.let { difficulty ->
                            Badge(
                                containerColor = when (difficulty.lowercase()) {
                                    "easy" -> MaterialTheme.colorScheme.tertiaryContainer
                                    "medium" -> MaterialTheme.colorScheme.secondaryContainer
                                    "hard" -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = when (difficulty.lowercase()) {
                                    "easy" -> MaterialTheme.colorScheme.onTertiaryContainer
                                    "medium" -> MaterialTheme.colorScheme.onSecondaryContainer
                                    "hard" -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ) {
                                Text(
                                    text = difficulty.replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        question.year?.let { year ->
                            Badge(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Text(
                                    text = "Year: $year",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        question.topic?.let { topic ->
                            Badge(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Text(
                                    text = topic,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    question.imageUrl?.let { imageUrl ->
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Question image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Answers",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    question.answers?.forEachIndexed { index, answer ->
                        AnswerOption(
                            answer = answer,
                            index = index,
                            isSelected = selectedAnswerId == answer.id,
                            showResult = showExplanation,
                            onClick = {
                                if (!showExplanation) {
                                    selectedAnswerId = answer.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showExplanation = !showExplanation },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedAnswerId != null || showExplanation
                    ) {
                        Icon(
                            imageVector = if (showExplanation) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showExplanation) "Hide Explanation" else "Reveal Answer"
                        )
                    }

                    AnimatedVisibility(
                        visible = showExplanation,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            val correctAnswer = question.answers?.find { it.isCorrect }
                            if (correctAnswer != null && !correctAnswer.explanation.isNullOrBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Explanation",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = correctAnswer.explanation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
}

@Composable
private fun AnswerOption(
    answer: Answer,
    index: Int,
    isSelected: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val optionLabel = ('A' + index).toString()

    val borderColor = when {
        showResult && answer.isCorrect -> MaterialTheme.colorScheme.tertiary
        showResult && isSelected && !answer.isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val containerColor = when {
        showResult && answer.isCorrect -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        showResult && isSelected && !answer.isCorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isSelected || (showResult && answer.isCorrect)) 2.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$optionLabel.",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(28.dp)
            )
            Text(
                text = answer.answerText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (showResult) {
                if (answer.isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                } else if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Incorrect",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
