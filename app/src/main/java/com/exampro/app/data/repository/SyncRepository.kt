package com.exampro.app.data.repository

import com.exampro.app.data.api.ExamApi
import com.exampro.app.data.api.QuestionApi
import com.exampro.app.data.api.SubjectApi
import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.db.entities.AnswerEntity
import com.exampro.app.data.db.entities.ExamEntity
import com.exampro.app.data.db.entities.QuestionEntity
import com.exampro.app.data.db.entities.SubjectEntity
import com.exampro.app.data.models.Answer
import com.exampro.app.data.models.Exam
import com.exampro.app.data.models.Question
import com.exampro.app.data.models.Subject
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val examApi: ExamApi,
    private val subjectApi: SubjectApi,
    private val questionApi: QuestionApi,
    private val examDao: ExamDao,
    private val subjectDao: SubjectDao,
    private val questionDao: QuestionDao,
    private val settingsRepository: SettingsRepository
) {
    suspend fun syncAllData(): Result<Unit> {
        return try {
            // 1. Fetch data from remote
            val examsResponse = examApi.getExams()
            val subjectsResponse = subjectApi.getSubjects()
            val questionsResponse = questionApi.getQuestions()

            if (examsResponse.isSuccessful && subjectsResponse.isSuccessful && questionsResponse.isSuccessful) {
                val remoteExams = examsResponse.body() ?: emptyList()
                val remoteSubjects = subjectsResponse.body() ?: emptyList()
                val remoteQuestions = questionsResponse.body() ?: emptyList()
                
                val maxQuestions = settingsRepository.maxPrefetch.first()

                // 2. Clear local database (except bookmarks if desired, but request says clear all)
                // We'll preserve bookmarks by merging them later if needed, but per request: "clear and refresh"
                val existingBookmarks = questionDao.getAllQuestionsList().filter { it.isBookmarked }

                examDao.deleteAll()
                subjectDao.deleteAll()
                questionDao.deleteAllQuestions()
                questionDao.deleteAllAnswers()

                // 3. Save new data
                examDao.insertAll(remoteExams.map { it.toEntity() })
                subjectDao.insertAll(remoteSubjects.map { it.toEntity() })
                
                // Limit questions based on settings
                val limitedQuestions = remoteQuestions.take(maxQuestions)
                val questionEntities = limitedQuestions.map { q ->
                    val isBookmarked = existingBookmarks.any { it.id == q.id }
                    q.toEntity(isBookmarked)
                }
                
                val answerEntities = mutableListOf<AnswerEntity>()
                limitedQuestions.forEach { q ->
                    q.answers?.forEach { a ->
                        answerEntities.add(a.toEntity())
                    }
                }

                questionDao.insertQuestionsWithAnswers(questionEntities, answerEntities)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch data from server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Exam.toEntity(): ExamEntity = ExamEntity(
        id = id,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )

    private fun Subject.toEntity(): SubjectEntity = SubjectEntity(
        id = id,
        examId = examId,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )

    private fun Question.toEntity(isBookmarked: Boolean): QuestionEntity = QuestionEntity(
        id = id,
        subjectId = subjectId,
        questionText = questionText,
        imageUrl = imageUrl,
        year = year,
        difficulty = difficulty,
        topic = topic,
        isBookmarked = isBookmarked,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )

    private fun Answer.toEntity(): AnswerEntity = AnswerEntity(
        id = id,
        questionId = questionId,
        answerText = answerText,
        isCorrect = isCorrect,
        explanation = explanation,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}
