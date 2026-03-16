package com.exampro.app.data.repository

import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.models.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val subjectDao: SubjectDao
) {
    fun getSubjectsFlow(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getSubjectsByExamFlow(examId: Int): Flow<List<Subject>> {
        return subjectDao.getSubjectsByExam(examId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getSubject(id: Int): Result<Subject> {
        val cached = subjectDao.getSubjectById(id)
        return if (cached != null) {
            Result.success(cached.toModel())
        } else {
            Result.failure(Exception("Subject not found in local database"))
        }
    }

    private fun com.exampro.app.data.db.entities.SubjectEntity.toModel(): Subject = Subject(
        id = id,
        examId = examId,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
