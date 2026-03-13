package com.exampro.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.exampro.app.data.db.entities.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY name ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams ORDER BY name ASC")
    suspend fun getAllExamsList(): List<ExamEntity>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: Int): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exams: List<ExamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exam: ExamEntity)

    @Query("DELETE FROM exams")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(exams: List<ExamEntity>) {
        deleteAll()
        insertAll(exams)
    }
}
