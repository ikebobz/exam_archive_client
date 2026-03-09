package com.exampro.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.exampro.app.data.db.entities.StudyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyProgressDao {
    @Query("SELECT * FROM study_progress ORDER BY completedAt DESC")
    fun getAllProgress(): Flow<List<StudyProgressEntity>>

    @Query("SELECT * FROM study_progress ORDER BY completedAt DESC")
    suspend fun getAllProgressList(): List<StudyProgressEntity>

    @Query("SELECT * FROM study_progress WHERE subjectId = :subjectId ORDER BY completedAt DESC")
    fun getProgressBySubject(subjectId: Int): Flow<List<StudyProgressEntity>>

    @Query("SELECT * FROM study_progress WHERE subjectId = :subjectId ORDER BY completedAt DESC")
    suspend fun getProgressBySubjectList(subjectId: Int): List<StudyProgressEntity>

    @Query("SELECT * FROM study_progress WHERE id = :id")
    suspend fun getProgressById(id: Int): StudyProgressEntity?

    @Query("SELECT AVG(score) FROM study_progress WHERE subjectId = :subjectId")
    suspend fun getAverageScoreBySubject(subjectId: Int): Float?

    @Query("SELECT AVG(score) FROM study_progress")
    suspend fun getOverallAverageScore(): Float?

    @Query("SELECT COUNT(*) FROM study_progress")
    suspend fun getTotalQuizCount(): Int

    @Query("SELECT SUM(totalQuestions) FROM study_progress")
    suspend fun getTotalQuestionsAnswered(): Int?

    @Query("SELECT SUM(correctAnswers) FROM study_progress")
    suspend fun getTotalCorrectAnswers(): Int?

    @Insert
    suspend fun insert(progress: StudyProgressEntity): Long

    @Query("DELETE FROM study_progress")
    suspend fun deleteAll()
}
