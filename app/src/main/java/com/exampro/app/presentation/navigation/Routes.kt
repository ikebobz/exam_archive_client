package com.exampro.app.presentation.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Dashboard : Routes("dashboard")
    object ExamList : Routes("exams?purpose={purpose}") {
        fun createRoute(purpose: String? = null) = 
            if (purpose != null) "exams?purpose=$purpose" else "exams"
    }
    object SubjectList : Routes("exams/{examId}/subjects?purpose={purpose}") {
        fun createRoute(examId: Int, purpose: String? = null) = 
            "exams/$examId/subjects" + (if (purpose != null) "?purpose=$purpose" else "")
    }
    object QuestionList : Routes("subjects/{subjectId}/questions") {
        fun createRoute(subjectId: Int) = "subjects/$subjectId/questions"
    }
    object Bookmarks : Routes("bookmarks")
    object QuestionDetail : Routes("questions/{questionId}?topic={topic}") {
        fun createRoute(questionId: Int, topic: String? = null) = 
            "questions/$questionId" + (if (topic != null) "?topic=$topic" else "")
    }
    object Quiz : Routes("quiz/{subjectId}") {
        fun createRoute(subjectId: Int) = "quiz/$subjectId"
    }
    object QuizResult : Routes("quiz-result/{subjectId}/{total}/{correct}") {
        fun createRoute(subjectId: Int, total: Int, correct: Int) =
            "quiz-result/$subjectId/$total/$correct"
    }
    object Profile : Routes("profile")
    object Settings : Routes("settings")
}
