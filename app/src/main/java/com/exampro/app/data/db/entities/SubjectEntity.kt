package com.exampro.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("examId")]
)
data class SubjectEntity(
    @PrimaryKey val id: Int,
    val examId: Int,
    val name: String,
    val code: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)
