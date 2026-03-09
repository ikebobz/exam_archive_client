package com.exampro.app.data.models

import com.google.gson.annotations.SerializedName

data class Question(
    val id: Int,
    @SerializedName("subjectId") val subjectId: Int,
    @SerializedName("questionText") val questionText: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    val year: Int?,
    val difficulty: String?,
    val topic: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    val answers: List<Answer>? = null
)

data class Answer(
    val id: Int,
    @SerializedName("questionId") val questionId: Int,
    @SerializedName("answerText") val answerText: String,
    @SerializedName("isCorrect") val isCorrect: Boolean,
    val explanation: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
