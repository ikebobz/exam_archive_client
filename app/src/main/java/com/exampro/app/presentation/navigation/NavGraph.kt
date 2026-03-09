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
import com.exampro.app.presentation.viewmodels.DashboardUiState
import com.exampro.app.presentation.screens.exam.ExamListScreen
import com.exampro.app.presentation.screens.profile.ProfileScreen
import com.exampro.app.presentation.screens.question.QuestionDetailScreen
import com.exampro.app.presentation.screens.question.QuestionListScreen
import com.exampro.app.presentation.screens.quiz.QuizResultScreen
import com.exampro.app.presentation.screens.quiz.QuizScreen
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
                    navController.navigate(Routes.ExamList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.Profile.route)
                },
                onRetry = { dashboardViewModel.refresh() }
            )
        }

        composable(Routes.ExamList.route) {
            ExamListScreen(
                onExamClick = { examId ->
                    navController.navigate(Routes.SubjectList.createRoute(examId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.SubjectList.route,
            arguments = listOf(navArgument("examId") { type = NavType.IntType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0
            SubjectListScreen(
                examId = examId,
                onSubjectClick = { subjectId ->
                    navController.navigate(Routes.QuestionList.createRoute(subjectId))
                },
                onBackClick = { navController.popBackStack() }
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
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.QuestionDetail.route,
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            QuestionDetailScreen(
                questionId = questionId,
                onBackClick = { navController.popBackStack() }
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
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
