package com.exampro.app.data.repository

import com.exampro.app.data.db.dao.StudyProgressDao
import com.exampro.app.data.db.entities.StudyProgressEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyProgressRepository @Inject constructor(
    private val studyProgressDao: StudyProgressDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun getAllProgressFlow(): Flow<List<StudyProgressEntity>> {
        return studyProgressDao.getAllProgress()
    }

    fun getProgressBySubjectFlow(subjectId: Int): Flow<List<StudyProgressEntity>> {
        return studyProgressDao.getProgressBySubject(subjectId)
    }

    suspend fun saveQuizResult(
        subjectId: Int,
        totalQuestions: Int,
        correctAnswers: Int
    ): Long {
        val score = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100f
        } else {
            0f
        }

        val progress = StudyProgressEntity(
            subjectId = subjectId,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            score = score,
            completedAt = dateFormat.format(Date())
        )

        return studyProgressDao.insert(progress)
    }

    suspend fun getAverageScoreBySubject(subjectId: Int): Float {
        return studyProgressDao.getAverageScoreBySubject(subjectId) ?: 0f
    }

    suspend fun getOverallAverageScore(): Float {
        return studyProgressDao.getOverallAverageScore() ?: 0f
    }

    suspend fun getTotalQuizCount(): Int {
        return studyProgressDao.getTotalQuizCount()
    }

    suspend fun getTotalQuestionsAnswered(): Int {
        return studyProgressDao.getTotalQuestionsAnswered() ?: 0
    }

    suspend fun getTotalCorrectAnswers(): Int {
        return studyProgressDao.getTotalCorrectAnswers() ?: 0
    }

    suspend fun getAllProgress(): List<StudyProgressEntity> {
        return studyProgressDao.getAllProgressList()
    }

    suspend fun getProgressBySubject(subjectId: Int): List<StudyProgressEntity> {
        return studyProgressDao.getProgressBySubjectList(subjectId)
    }

    suspend fun clearAllProgress() {
        studyProgressDao.deleteAll()
    }
}
