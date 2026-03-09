package com.exampro.app.data.repository

import com.exampro.app.data.api.ExamApi
import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.db.entities.ExamEntity
import com.exampro.app.data.models.Exam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val examApi: ExamApi,
    private val examDao: ExamDao
) {
    fun getExamsFlow(): Flow<List<Exam>> {
        return examDao.getAllExams().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun refreshExams(): Result<List<Exam>> {
        return try {
            val response = examApi.getExams()
            if (response.isSuccessful && response.body() != null) {
                val exams = response.body()!!
                examDao.deleteAll()
                examDao.insertAll(exams.map { it.toEntity() })
                Result.success(exams)
            } else {
                val cached = examDao.getAllExamsList()
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toModel() })
                } else {
                    Result.failure(Exception("Failed to fetch exams: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            val cached = examDao.getAllExamsList()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toModel() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getExam(id: Int): Result<Exam> {
        return try {
            val response = examApi.getExam(id)
            if (response.isSuccessful && response.body() != null) {
                val exam = response.body()!!
                examDao.insert(exam.toEntity())
                Result.success(exam)
            } else {
                val cached = examDao.getExamById(id)
                if (cached != null) {
                    Result.success(cached.toModel())
                } else {
                    Result.failure(Exception("Exam not found"))
                }
            }
        } catch (e: Exception) {
            val cached = examDao.getExamById(id)
            if (cached != null) {
                Result.success(cached.toModel())
            } else {
                Result.failure(e)
            }
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

    private fun ExamEntity.toModel(): Exam = Exam(
        id = id,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
