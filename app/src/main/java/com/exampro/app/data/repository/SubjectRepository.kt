package com.exampro.app.data.repository

import com.exampro.app.data.api.SubjectApi
import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.db.entities.SubjectEntity
import com.exampro.app.data.models.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val subjectApi: SubjectApi,
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

    suspend fun refreshSubjects(): Result<List<Subject>> {
        return try {
            val response = subjectApi.getSubjects()
            if (response.isSuccessful && response.body() != null) {
                val subjects = response.body()!!
                subjectDao.replaceAll(subjects.map { it.toEntity() })
                Result.success(subjects)
            } else {
                val cached = subjectDao.getAllSubjectsList()
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toModel() })
                } else {
                    Result.failure(Exception("Failed to fetch subjects: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            val cached = subjectDao.getAllSubjectsList()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toModel() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getSubjectsByExam(examId: Int): Result<List<Subject>> {
        return try {
            val response = subjectApi.getSubjects()
            if (response.isSuccessful && response.body() != null) {
                val allSubjects = response.body()!!
                val filtered = allSubjects.filter { it.examId == examId }
                subjectDao.replaceByExam(examId, filtered.map { it.toEntity() })
                Result.success(filtered)
            } else {
                val cached = subjectDao.getSubjectsByExamList(examId)
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toModel() })
                } else {
                    Result.failure(Exception("Failed to fetch subjects: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            val cached = subjectDao.getSubjectsByExamList(examId)
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toModel() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getSubject(id: Int): Result<Subject> {
        return try {
            val response = subjectApi.getSubject(id)
            if (response.isSuccessful && response.body() != null) {
                val subject = response.body()!!
                subjectDao.insert(subject.toEntity())
                Result.success(subject)
            } else {
                val cached = subjectDao.getSubjectById(id)
                if (cached != null) {
                    Result.success(cached.toModel())
                } else {
                    Result.failure(Exception("Subject not found"))
                }
            }
        } catch (e: Exception) {
            val cached = subjectDao.getSubjectById(id)
            if (cached != null) {
                Result.success(cached.toModel())
            } else {
                Result.failure(e)
            }
        }
    }

    private fun Subject.toEntity(): SubjectEntity = SubjectEntity(
        id = id,
        examId = examId,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )

    private fun SubjectEntity.toModel(): Subject = Subject(
        id = id,
        examId = examId,
        name = name,
        code = code,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
