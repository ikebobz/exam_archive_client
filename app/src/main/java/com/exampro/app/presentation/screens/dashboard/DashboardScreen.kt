package com.exampro.app.presentation.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exampro.app.presentation.components.ErrorMessage
import com.exampro.app.presentation.components.LoadingIndicator
import com.exampro.app.presentation.components.StatsCard
import com.exampro.app.presentation.components.TopBar
import com.exampro.app.presentation.viewmodels.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onNavigateToExams: () -> Unit = {},
    onNavigateToSubjects: () -> Unit = {},
    onNavigateToQuestions: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Dashboard",
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is DashboardUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }

            is DashboardUiState.Error -> {
                ErrorMessage(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is DashboardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    StatsCard(
                        label = "Total Exams",
                        value = uiState.stats.totalExams.toString(),
                        icon = Icons.AutoMirrored.Filled.Assignment
                    )

                    StatsCard(
                        label = "Total Subjects",
                        value = uiState.stats.totalSubjects.toString(),
                        icon = Icons.Default.Book
                    )

                    StatsCard(
                        label = "Total Questions",
                        value = uiState.stats.totalQuestions.toString(),
                        icon = Icons.Default.QuestionAnswer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Quick Links",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickLinkCard(
                            title = "Exams",
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            onClick = onNavigateToExams,
                            modifier = Modifier.weight(1f)
                        )
                        QuickLinkCard(
                            title = "Subjects",
                            icon = Icons.Default.Book,
                            onClick = onNavigateToSubjects,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickLinkCard(
                            title = "Questions",
                            icon = Icons.Default.QuestionAnswer,
                            onClick = onNavigateToQuestions,
                            modifier = Modifier.weight(1f)
                        )
                        QuickLinkCard(
                            title = "Study",
                            icon = Icons.Default.School,
                            onClick = onNavigateToExams,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No recent activity",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start studying to see your progress here",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickLinkCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.height(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
