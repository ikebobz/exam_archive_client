package com.exampro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.exampro.app.presentation.navigation.NavGraph
import com.exampro.app.presentation.screens.settings.SettingsScreen
import com.exampro.app.presentation.theme.ExamProTheme
import com.exampro.app.presentation.viewmodels.AuthUiState
import com.exampro.app.presentation.viewmodels.AuthViewModel
import com.exampro.app.presentation.viewmodels.MainViewModel
import com.exampro.app.presentation.viewmodels.SyncState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val syncState by mainViewModel.syncState.collectAsState()
            val authState by authViewModel.uiState.collectAsState()
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            var showSettingsInSplash by remember { mutableStateOf(false) }

            ExamProTheme(darkTheme = isDarkMode) {
                if (showSettingsInSplash) {
                    SettingsScreen(
                        onBackClick = { showSettingsInSplash = false }
                    )
                } else {
                    when (authState) {
                        is AuthUiState.Authenticated -> {
                            // User is authenticated, check sync state
                            when (syncState) {
                                is SyncState.Idle -> {
                                    LaunchedEffect(Unit) {
                                        mainViewModel.performInitialSync()
                                    }
                                    LoadingSplashScreen(onOpenSettings = { showSettingsInSplash = true })
                                }
                                is SyncState.Syncing -> {
                                    LoadingSplashScreen(onOpenSettings = { showSettingsInSplash = true })
                                }
                                is SyncState.Error -> {
                                    SyncErrorScreen(
                                        message = (syncState as SyncState.Error).message,
                                        onRetry = { mainViewModel.performInitialSync() },
                                        onOpenSettings = { showSettingsInSplash = true }
                                    )
                                }
                                is SyncState.Success -> {
                                    MainContent(isDarkMode = isDarkMode, onToggleDarkMode = { isDarkMode = it })
                                }
                            }
                        }
                        is AuthUiState.Loading -> {
                            LoadingSplashScreen(onOpenSettings = { showSettingsInSplash = true })
                        }
                        else -> {
                            // Not authenticated, show login/register
                            MainContent(isDarkMode = isDarkMode, onToggleDarkMode = { isDarkMode = it })
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MainContent(isDarkMode: Boolean, onToggleDarkMode: (Boolean) -> Unit) {
        val navController = rememberNavController()
        NavGraph(
            navController = navController,
            authViewModel = authViewModel,
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode
        )
    }
}

@Composable
fun LoadingSplashScreen(onOpenSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ExamPro",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SyncErrorScreen(message: String, onRetry: () -> Unit, onOpenSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sync Failed",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
