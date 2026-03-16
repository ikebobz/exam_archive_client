package com.exampro.app.data.repository

import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.models.Answer
import com.exampro.app.data.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
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

    fun getQuestionsBySubjectFlow(subjectId: Int): Flow<List<Question>> {
        return questionDao.getQuestionsBySubject(subjectId).map { entities ->
            // Note: In a real app, you'd fetch answers here too if needed for the list view
            entities.map { it.toModel() }
        }
    }

    suspend fun getQuestion(id: Int): Result<Question> {
        val cached = questionDao.getQuestionById(id)
        return if (cached != null) {
            val answers = questionDao.getAnswersByQuestion(id)
            Result.success(cached.toModel(answers.map { it.toModel() }))
        } else {
            Result.failure(Exception("Question not found in local database"))
        }
    }

    suspend fun toggleBookmark(question: Question): Boolean {
        val existing = questionDao.getQuestionById(question.id)
        return if (existing != null && existing.isBookmarked) {
            // Update to false instead of deleting, as we want to keep the question if it was prefetched
            questionDao.insertQuestion(existing.copy(isBookmarked = false))
            false
        } else if (existing != null) {
            questionDao.insertQuestion(existing.copy(isBookmarked = true))
            true
        } else {
            // This shouldn't happen with full prefetch, but for safety:
            questionDao.insertQuestion(question.toEntity(isBookmarked = true))
            question.answers?.let { answers ->
                questionDao.insertAllAnswers(answers.map { it.toEntity() })
            }
            true
        }
    }

    private fun Question.toEntity(isBookmarked: Boolean = false): com.exampro.app.data.db.entities.QuestionEntity = com.exampro.app.data.db.entities.QuestionEntity(
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

    private fun com.exampro.app.data.db.entities.QuestionEntity.toModel(answers: List<Answer>? = null): Question = Question(
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

    private fun com.exampro.app.data.db.entities.AnswerEntity.toModel(): Answer = Answer(
        id = id,
        questionId = questionId,
        answerText = answerText,
        isCorrect = isCorrect,
        explanation = explanation,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Answer.toEntity(): com.exampro.app.data.db.entities.AnswerEntity = com.exampro.app.data.db.entities.AnswerEntity(
        id = id,
        questionId = questionId,
        answerText = answerText,
        isCorrect = isCorrect,
        explanation = explanation,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}
