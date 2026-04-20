package com.exampro.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exampro.app.data.db.entities.BookmarkEntity
import com.exampro.app.data.db.entities.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND questionId = :questionId")
    suspend fun deleteBookmark(userId: String, questionId: Int)

    @Query("SELECT * FROM bookmarks WHERE userId = :userId AND questionId = :questionId")
    suspend fun getBookmark(userId: String, questionId: Int): BookmarkEntity?

    @Query("SELECT questions.* FROM questions INNER JOIN bookmarks ON questions.id = bookmarks.questionId WHERE bookmarks.userId = :userId ORDER BY bookmarks.createdAt DESC")
    fun getBookmarkedQuestions(userId: String): Flow<List<QuestionEntity>>

    @Query("SELECT questionId FROM bookmarks WHERE userId = :userId")
    suspend fun getBookmarkedQuestionIds(userId: String): List<Int>
}
