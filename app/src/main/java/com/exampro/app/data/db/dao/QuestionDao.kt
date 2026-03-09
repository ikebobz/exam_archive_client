package com.exampro.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.exampro.app.data.db.entities.AnswerEntity
import com.exampro.app.data.db.entities.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY id DESC")
    suspend fun getAllQuestionsList(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY id DESC")
    fun getQuestionsBySubject(subjectId: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY id DESC")
    suspend fun getQuestionsBySubjectList(subjectId: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): QuestionEntity?

    @Query("SELECT * FROM answers WHERE questionId = :questionId")
    suspend fun getAnswersByQuestion(questionId: Int): List<AnswerEntity>

    @Query("SELECT * FROM answers WHERE questionId IN (:questionIds)")
    suspend fun getAnswersByQuestions(questionIds: List<Int>): List<AnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnswers(answers: List<AnswerEntity>)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM answers")
    suspend fun deleteAllAnswers()

    @Query("DELETE FROM questions WHERE subjectId = :subjectId")
    suspend fun deleteQuestionsBySubject(subjectId: Int)

    @Query("DELETE FROM answers WHERE questionId IN (SELECT id FROM questions WHERE subjectId = :subjectId)")
    suspend fun deleteAnswersBySubject(subjectId: Int)

    @Transaction
    suspend fun insertQuestionsWithAnswers(
        questions: List<QuestionEntity>,
        answers: List<AnswerEntity>
    ) {
        insertAllQuestions(questions)
        insertAllAnswers(answers)
    }
}
