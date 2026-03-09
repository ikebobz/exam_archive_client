package com.exampro.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val subjectId: Int,
    val questionText: String,
    val imageUrl: String?,
    val year: Int?,
    val difficulty: String?,
    val topic: String?,
    val createdAt: String,
    val updatedAt: String
)
