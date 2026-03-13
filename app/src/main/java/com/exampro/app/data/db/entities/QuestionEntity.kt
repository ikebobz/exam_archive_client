package com.exampro.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val subjectId: Int,
    val questionText: String,
    val imageUrl: String?,
    val year: Int?,
    val difficulty: String?,
    val topic: String?,
    val isBookmarked: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
