package com.exampro.app.data.api

import com.exampro.app.data.models.AuthResponse
import com.exampro.app.data.models.LoginRequest
import com.exampro.app.data.models.RegisterRequest
import com.exampro.app.data.models.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/auth/user")
    suspend fun getProfile(): Response<UserProfile>
}
