package com.exampro.app.data.repository

import android.content.SharedPreferences
import com.exampro.app.data.api.AuthApi
import com.exampro.app.data.db.AppDatabase
import com.exampro.app.data.models.AuthResponse
import com.exampro.app.data.models.DashboardStats
import com.exampro.app.data.models.LoginRequest
import com.exampro.app.data.models.RegisterRequest
import com.exampro.app.data.models.UserProfile
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences,
    private val database: AppDatabase
) {
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_TOTAL_EXAMS = "total_exams"
        private const val KEY_TOTAL_SUBJECTS = "total_subjects"
        private const val KEY_TOTAL_QUESTIONS = "total_questions"
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveSession(authResponse.user.id, authResponse.user.email)
                Result.success(authResponse)
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid credentials. Please check your email and password."
                    404 -> "Account not found. Please register first."
                    500 -> "Server error. Please try again later."
                    else -> {
                        // 2. Fallback: If it's not a common error, try to read the server message
                        try {
                            val errorBody = response.errorBody()?.string()
                            val json = JSONObject(errorBody ?: "{}")
                            json.optString("message", "Login failed. Please try again.")
                        } catch (e: Exception) {
                            "Login failed. Please try again."
                        }
                    }
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Could not connect to the server. Please check your IP settings and internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception("Service is unreachable, please retry again later!!"))
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
                val errorMsg = try {
                    val errorBody = response.errorBody()?.string()
                    val json = JSONObject(errorBody ?: "{}")
                    json.optString("message", "Registration failed. Please check your details.")
                } catch (e: Exception) {
                    "Registration failed. Email might already be in use."
                }
                Result.failure(Exception(errorMsg))
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
                Result.failure(Exception("Failed to get profile"))
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

    fun saveStats(stats: DashboardStats) {
        sharedPreferences.edit()
            .putInt(KEY_TOTAL_EXAMS, stats.totalExams)
            .putInt(KEY_TOTAL_SUBJECTS, stats.totalSubjects)
            .putInt(KEY_TOTAL_QUESTIONS, stats.totalQuestions)
            .apply()
    }

    fun getCachedStats(): DashboardStats {
        return DashboardStats(
            totalExams = sharedPreferences.getInt(KEY_TOTAL_EXAMS, 0),
            totalSubjects = sharedPreferences.getInt(KEY_TOTAL_SUBJECTS, 0),
            totalQuestions = sharedPreferences.getInt(KEY_TOTAL_QUESTIONS, 0)
        )
    }
}
