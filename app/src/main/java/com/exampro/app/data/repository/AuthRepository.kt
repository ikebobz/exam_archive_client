package com.exampro.app.data.repository

import android.content.SharedPreferences
import com.exampro.app.data.api.AuthApi
import com.exampro.app.data.models.AuthResponse
import com.exampro.app.data.models.LoginRequest
import com.exampro.app.data.models.RegisterRequest
import com.exampro.app.data.models.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveSession(authResponse.user.id, authResponse.user.email)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, firstName: String?, lastName: String?): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(email, password, firstName, lastName))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveSession(authResponse.user.id, authResponse.user.email)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            authApi.logout()
            clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            clearSession()
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = authApi.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getSavedUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun getSavedUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    private fun saveSession(userId: String, email: String) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    private fun clearSession() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .apply()
    }
}
