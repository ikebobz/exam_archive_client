package com.exampro.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val code: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)
