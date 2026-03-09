package com.exampro.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class AnswerEntity(
    @PrimaryKey val id: Int,
    val questionId: Int,
    val answerText: String,
    val isCorrect: Boolean,
    val explanation: String?,
    val createdAt: String,
    val updatedAt: String
)
