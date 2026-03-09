package com.exampro.app.data.models

import com.google.gson.annotations.SerializedName

data class Exam(
    val id: Int,
    val name: String,
    val code: String,
    val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
