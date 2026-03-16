package com.exampro.app.data.repository

import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.models.Exam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val examDao: ExamDao
) {
    fun getExamsFlow(): Flow<List<Exam>> {
        return examDao.getAllExams().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getExam(id: Int): Result<Exam> {
        val cached = examDao.getExamById(id)
        return if (cached != null) {
            Result.success(cached.toModel())
        } else {
            Result.failure(Exception("Exam not found in local database"))
        }
    }

    private fun ExamEntityToModel(entity: com.exampro.app.data.db.entities.ExamEntity): Exam = Exam(
        id = entity.id,
        name = entity.name,
        code = entity.code,
        description = entity.description,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
    
    // Extension function for consistency
    private fun com.exampro.app.data.db.entities.ExamEntity.toModel(): Exam = Exam(
        id = id,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
