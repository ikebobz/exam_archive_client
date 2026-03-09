package com.exampro.app.presentation.screens.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.LoadingIndicator
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.viewmodels.QuizUiState
import com.exampro.app.presentation.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    subjectId: Int,
    onBack: () -> Unit,
    onQuizFinished: (score: Int, total: Int) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(subjectId) {
        viewModel.loadQuiz(subjectId)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Quiz",
                onBackClick = onBack,
                actions = {
                    if (uiState is QuizUiState.InProgress) {
                        val state = uiState as QuizUiState.InProgress
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatTime(state.elapsedSeconds),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> LoadingIndicator()

                is QuizUiState.Ready -> ReadyContent(
                    totalQuestions = state.totalQuestions,
                    onStart = { viewModel.startQuiz() }
                )

                is QuizUiState.InProgress -> InProgressContent(
                    state = state,
                    onSelectAnswer = { viewModel.selectAnswer(it) },
                    onNext = { viewModel.nextQuestion() }
                )

                is QuizUiState.Finished -> {
                    LaunchedEffect(state) {
                        onQuizFinished(state.score, state.totalQuestions)
                    }
                }

                is QuizUiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadQuiz(subjectId) }
                )
            }
        }
    }
}

@Composable
private fun ReadyContent(
    totalQuestions: Int,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.height(64.dp).width(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ready to Start",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$totalQuestions questions",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Start Quiz")
        }
    }
}

@Composable
private fun InProgressContent(
    state: QuizUiState.InProgress,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    val question = state.currentQuestion
    val progress = (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${state.currentIndex + 1} of ${state.totalQuestions}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Score: ${state.score}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = question.question.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                question.question.imageUrl?.let { url ->
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = url,
                        contentDescription = "Question image",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        question.answers.forEach { answer ->
            val isSelected = state.selectedAnswerId == answer.id
            val isAnswered = state.isAnswered
            val isCorrect = answer.isCorrect

            val containerColor by animateColorAsState(
                targetValue = when {
                    isAnswered && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                    isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "answer_color"
            )

            val borderColor by animateColorAsState(
                targetValue = when {
                    isAnswered && isCorrect -> MaterialTheme.colorScheme.primary
                    isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.outline
                },
                label = "border_color"
            )

            OutlinedCard(
                onClick = {
                    if (!isAnswered) onSelectAnswer(answer.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
                border = BorderStroke(
                    width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
                    color = borderColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = answer.answerText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isAnswered) {
                        if (isCorrect) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else if (isSelected) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Incorrect",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (state.isAnswered) {
            val currentAnswer = question.answers.find { it.id == state.selectedAnswerId }
            val correctAnswer = question.answers.find { it.isCorrect }
            val explanation = correctAnswer?.explanation ?: currentAnswer?.explanation

            if (!explanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Explanation",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (state.currentIndex + 1 >= state.totalQuestions) "Finish Quiz"
                    else "Next Question"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
