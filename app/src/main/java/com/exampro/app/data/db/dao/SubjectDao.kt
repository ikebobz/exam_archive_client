package com.exampro.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.exampro.app.data.db.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    suspend fun getAllSubjectsList(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE examId = :examId ORDER BY name ASC")
    fun getSubjectsByExam(examId: Int): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE examId = :examId ORDER BY name ASC")
    suspend fun getSubjectsByExamList(examId: Int): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: SubjectEntity)

    @Query("DELETE FROM subjects")
    suspend fun deleteAll()

    @Query("DELETE FROM subjects WHERE examId = :examId")
    suspend fun deleteByExam(examId: Int)

    @Transaction
    suspend fun replaceAll(subjects: List<SubjectEntity>) {
        deleteAll()
        insertAll(subjects)
    }

    @Transaction
    suspend fun replaceByExam(examId: Int, subjects: List<SubjectEntity>) {
        deleteByExam(examId)
        insertAll(subjects)
    }
}
