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
    fun getQuestionsFlow(): Flow<List<Question>> {
        return questionDao.getAllQuestions().map { entities ->
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
                val questions = response.body()!!
                cacheQuestions(questions)
                Result.success(questions)
            } else {
                getQuestionsFromCache()
            }
        } catch (e: Exception) {
            val cached = getQuestionsFromCache()
            if (cached.isSuccess) cached else Result.failure(e)
        }
    }

    suspend fun getQuestionsBySubject(subjectId: Int): Result<List<Question>> {
        return try {
            val response = questionApi.getQuestions()
            if (response.isSuccessful && response.body() != null) {
                val allQuestions = response.body()!!
                val filtered = allQuestions.filter { it.subjectId == subjectId }
                questionDao.deleteAnswersBySubject(subjectId)
                questionDao.deleteQuestionsBySubject(subjectId)
                cacheQuestionsList(filtered)
                Result.success(filtered)
            } else {
                getQuestionsBySubjectFromCache(subjectId)
            }
        } catch (e: Exception) {
            val cached = getQuestionsBySubjectFromCache(subjectId)
            if (cached.isSuccess) cached else Result.failure(e)
        }
    }

    suspend fun getQuestion(id: Int): Result<Question> {
        return try {
            val response = questionApi.getQuestion(id)
            if (response.isSuccessful && response.body() != null) {
                val question = response.body()!!
                questionDao.insertQuestion(question.toEntity())
                question.answers?.let { answers ->
                    questionDao.insertAllAnswers(answers.map { it.toEntity() })
                }
                Result.success(question)
            } else {
                getQuestionFromCache(id)
            }
        } catch (e: Exception) {
            val cached = getQuestionFromCache(id)
            if (cached.isSuccess) cached else Result.failure(e)
        }
    }

    private suspend fun cacheQuestions(questions: List<Question>) {
        questionDao.deleteAllAnswers()
        questionDao.deleteAllQuestions()
        cacheQuestionsList(questions)
    }

    private suspend fun cacheQuestionsList(questions: List<Question>) {
        val questionEntities = questions.map { it.toEntity() }
        val answerEntities = questions.flatMap { question ->
            question.answers?.map { it.toEntity() } ?: emptyList()
        }
        questionDao.insertQuestionsWithAnswers(questionEntities, answerEntities)
    }

    private suspend fun getQuestionsFromCache(): Result<List<Question>> {
        val cached = questionDao.getAllQuestionsList()
        return if (cached.isNotEmpty()) {
            val questionIds = cached.map { it.id }
            val allAnswers = questionDao.getAnswersByQuestions(questionIds)
            val answersByQuestion = allAnswers.groupBy { it.questionId }
            Result.success(cached.map { entity ->
                entity.toModel(answersByQuestion[entity.id]?.map { it.toModel() })
            })
        } else {
            Result.failure(Exception("No cached questions available"))
        }
    }

    private suspend fun getQuestionsBySubjectFromCache(subjectId: Int): Result<List<Question>> {
        val cached = questionDao.getQuestionsBySubjectList(subjectId)
        return if (cached.isNotEmpty()) {
            val questionIds = cached.map { it.id }
            val allAnswers = questionDao.getAnswersByQuestions(questionIds)
            val answersByQuestion = allAnswers.groupBy { it.questionId }
            Result.success(cached.map { entity ->
                entity.toModel(answersByQuestion[entity.id]?.map { it.toModel() })
            })
        } else {
            Result.failure(Exception("No cached questions available for this subject"))
        }
    }

    private suspend fun getQuestionFromCache(id: Int): Result<Question> {
        val cached = questionDao.getQuestionById(id)
        return if (cached != null) {
            val answers = questionDao.getAnswersByQuestion(id)
            Result.success(cached.toModel(answers.map { it.toModel() }))
        } else {
            Result.failure(Exception("Question not found"))
        }
    }

    private fun Question.toEntity(): QuestionEntity = QuestionEntity(
        id = id,
        subjectId = subjectId,
        questionText = questionText,
        imageUrl = imageUrl,
        year = year,
        difficulty = difficulty,
        topic = topic,
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
