package com.exampro.app.data.repository

import com.exampro.app.data.api.QuestionApi
import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.db.entities.AnswerEntity
import com.exampro.app.data.db.entities.QuestionEntity
import com.exampro.app.data.models.Answer
import com.exampro.app.data.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val questionApi: QuestionApi,
    private val questionDao: QuestionDao
) {
    fun getBookmarkedQuestionsFlow(): Flow<List<Question>> {
        return questionDao.getBookmarkedQuestions().map { entities ->
            val questionIds = entities.map { it.id }
            val allAnswers = if (questionIds.isNotEmpty()) {
                questionDao.getAnswersByQuestions(questionIds)
            } else {
                emptyList()
            }
            val answersByQuestion = allAnswers.groupBy { it.questionId }
            entities.map { entity ->
                entity.toModel(answersByQuestion[entity.id]?.map { it.toModel() })
            }
        }
    }

    suspend fun refreshQuestions(): Result<List<Question>> {
        return try {
            val response = questionApi.getQuestions()
            if (response.isSuccessful && response.body() != null) {
                val allQuestions = response.body()!!
                val bookmarkedIds = questionDao.getAllQuestionsList()
                    .filter { it.isBookmarked }
                    .map { it.id }.toSet()
                val questionsWithBookmarks = allQuestions.map { 
                    it.copy(isBookmarked = bookmarkedIds.contains(it.id))
                }
                Result.success(questionsWithBookmarks)
            } else {
                Result.failure(Exception("Failed to fetch questions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuestionsBySubject(subjectId: Int): Result<List<Question>> {
        return try {
            val response = questionApi.getQuestions()
            if (response.isSuccessful && response.body() != null) {
                val allQuestions = response.body()!!
                val filtered = allQuestions.filter { it.subjectId == subjectId }
                val bookmarkedIds = questionDao.getAllQuestionsList()
                    .filter { it.isBookmarked }
                    .map { it.id }.toSet()
                val questionsWithBookmarks = filtered.map { 
                    it.copy(isBookmarked = bookmarkedIds.contains(it.id))
                }
                Result.success(questionsWithBookmarks)
            } else {
                Result.failure(Exception("Failed to fetch questions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuestion(id: Int): Result<Question> {
        return try {
            val response = questionApi.getQuestion(id)
            if (response.isSuccessful && response.body() != null) {
                val question = response.body()!!
                val existing = questionDao.getQuestionById(id)
                val isBookmarked = existing?.isBookmarked ?: false
                Result.success(question.copy(isBookmarked = isBookmarked))
            } else {
                val cached = getQuestionFromCache(id)
                if (cached.isSuccess) cached else Result.failure(Exception("Question not found"))
            }
        } catch (e: Exception) {
            val cached = getQuestionFromCache(id)
            if (cached.isSuccess) cached else Result.failure(e)
        }
    }

    suspend fun toggleBookmark(question: Question): Boolean {
        val existing = questionDao.getQuestionById(question.id)
        return if (existing != null && existing.isBookmarked) {
            // If it was only there because it was bookmarked, we can delete it.
            // But if we use the table for general caching too, we might want to just update isBookmarked = false.
            // Given the current implementation of toggleBookmark, it seems it deletes it.
            questionDao.deleteAnswersByQuestion(question.id)
            questionDao.deleteQuestionById(question.id)
            false
        } else {
            questionDao.insertQuestion(question.toEntity(isBookmarked = true))
            question.answers?.let { answers ->
                questionDao.insertAllAnswers(answers.map { it.toEntity() })
            }
            true
        }
    }

    private suspend fun getQuestionFromCache(id: Int): Result<Question> {
        val cached = questionDao.getQuestionById(id)
        return if (cached != null) {
            val answers = questionDao.getAnswersByQuestion(id)
            Result.success(cached.toModel(answers.map { it.toModel() }))
        } else {
            Result.failure(Exception("Question not found in cache"))
        }
    }

    private fun Question.toEntity(isBookmarked: Boolean = false): QuestionEntity = QuestionEntity(
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

    private fun QuestionEntity.toModel(answers: List<Answer>? = null): Question = Question(
        id = id,
        subjectId = subjectId,
        questionText = questionText,
        imageUrl = imageUrl,
        year = year,
        difficulty = difficulty,
        topic = topic,
        isBookmarked = isBookmarked,
        createdAt = createdAt,
        updatedAt = updatedAt,
        answers = answers
    )

    private fun AnswerEntity.toModel(): Answer = Answer(
        id = id,
        questionId = questionId,
        answerText = answerText,
        isCorrect = isCorrect,
        explanation = explanation,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
