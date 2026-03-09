package com.exampro.app.data.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val user: AuthUser
)

data class AuthUser(
    val id: String,
    val email: String
)

data class UserProfile(
    val id: String,
    val email: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
