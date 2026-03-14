package com.exampro.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.exampro.app.presentation.screens.auth.LoginScreen
import com.exampro.app.presentation.screens.auth.LoginUiState
import com.exampro.app.presentation.screens.auth.RegisterScreen
import com.exampro.app.presentation.screens.auth.RegisterUiState
import com.exampro.app.presentation.screens.dashboard.DashboardScreen
import com.exampro.app.presentation.screens.exam.ExamListScreen
import com.exampro.app.presentation.screens.profile.ProfileScreen
import com.exampro.app.presentation.screens.question.QuestionDetailScreen
import com.exampro.app.presentation.screens.question.QuestionListScreen
import com.exampro.app.presentation.screens.quiz.QuizResultScreen
import com.exampro.app.presentation.screens.quiz.QuizScreen
import com.exampro.app.presentation.screens.settings.SettingsScreen
import com.exampro.app.presentation.screens.subject.SubjectListScreen
import com.exampro.app.presentation.viewmodels.AuthUiState
import com.exampro.app.presentation.viewmodels.AuthViewModel
import com.exampro.app.presentation.viewmodels.DashboardViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val startDestination = when (authState) {
        is AuthUiState.Authenticated -> Routes.Dashboard.route
        else -> Routes.Login.route
    }

    val onGoHome: () -> Unit = {
        navController.navigate(Routes.Dashboard.route) {
            popUpTo(Routes.Dashboard.route) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            val loginState = when (authState) {
                is AuthUiState.Loading -> LoginUiState.Loading
                is AuthUiState.Authenticated -> LoginUiState.Success()
                is AuthUiState.Error -> LoginUiState.Error((authState as AuthUiState.Error).message)
                else -> LoginUiState.Idle
            }
            LoginScreen(
                uiState = loginState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            val registerState = when (authState) {
                is AuthUiState.Loading -> RegisterUiState.Loading
                is AuthUiState.Authenticated -> RegisterUiState.Success()
                is AuthUiState.Error -> RegisterUiState.Error((authState as AuthUiState.Error).message)
                else -> RegisterUiState.Idle
            }
            RegisterScreen(
                uiState = registerState,
                onRegister = { email, password, firstName, lastName ->
                    authViewModel.register(email, password, firstName, lastName)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            val dashboardState by dashboardViewModel.uiState.collectAsState()
            DashboardScreen(
                uiState = dashboardState,
                onNavigateToExams = {
                    navController.navigate(Routes.ExamList.createRoute())
                },
                onNavigateToSubjects = {
                    navController.navigate(Routes.ExamList.createRoute(purpose = "subjects"))
                },
                onNavigateToBookmarks = {
                    navController.navigate(Routes.Bookmarks.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.Profile.route)
                },
                onRetry = { dashboardViewModel.refresh() }
            )
        }

        composable(
            route = Routes.ExamList.route,
            arguments = listOf(navArgument("purpose") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            ExamListScreen(
                onExamClick = { examId, purpose ->
                    navController.navigate(Routes.SubjectList.createRoute(examId, purpose))
                },
                onBackClick = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(
            route = Routes.SubjectList.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.IntType },
                navArgument("purpose") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0
            SubjectListScreen(
                examId = examId,
                onSubjectClick = { subjectId, purpose ->
                    if (purpose == "questions") {
                        navController.navigate(Routes.QuestionList.createRoute(subjectId))
                    } else {
                        navController.navigate(Routes.QuestionList.createRoute(subjectId))
                    }
                },
                onBackClick = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(
            route = Routes.QuestionList.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            QuestionListScreen(
                subjectId = subjectId,
                onQuestionClick = { questionId ->
                    navController.navigate(Routes.QuestionDetail.createRoute(questionId))
                },
                onBackClick = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(Routes.Bookmarks.route) {
            QuestionListScreen(
                subjectId = -1,
                onQuestionClick = { questionId ->
                    navController.navigate(Routes.QuestionDetail.createRoute(questionId))
                },
                onBackClick = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(
            route = Routes.QuestionDetail.route,
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            QuestionDetailScreen(
                questionId = questionId,
                onBackClick = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(
            route = Routes.Quiz.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            QuizScreen(
                subjectId = subjectId,
                onBack = { navController.popBackStack() },
                onHomeClick = onGoHome,
                onQuizFinished = { score, total ->
                    navController.navigate(
                        Routes.QuizResult.createRoute(subjectId, total, score)
                    ) {
                        popUpTo(Routes.Quiz.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.QuizResult.route,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("correct") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            QuizResultScreen(
                score = correct,
                totalQuestions = total,
                onRetryQuiz = {
                    navController.navigate(Routes.Quiz.createRoute(subjectId)) {
                        popUpTo(Routes.QuizResult.route) { inclusive = true }
                    }
                },
                onBackToSubjects = {
                    navController.popBackStack(Routes.ExamList.route, false)
                },
                onBack = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onHomeClick = onGoHome
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                //onHomeClick = onGoHome
            )
        }
    }
}
