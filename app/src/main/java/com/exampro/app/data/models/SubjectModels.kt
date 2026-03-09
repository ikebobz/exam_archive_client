package com.exampro.app.data.models

import com.google.gson.annotations.SerializedName

data class Subject(
    val id: Int,
    @SerializedName("examId") val examId: Int,
    val name: String,
    val code: String,
    val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
