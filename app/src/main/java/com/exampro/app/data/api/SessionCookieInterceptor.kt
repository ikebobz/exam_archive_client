package com.exampro.app.data.api

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class SessionCookieInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    companion object {
        private const val PREF_COOKIES = "session_cookies"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val cookies = sharedPreferences.getStringSet(PREF_COOKIES, emptySet())
        if (!cookies.isNullOrEmpty()) {
            requestBuilder.addHeader("Cookie", cookies.joinToString("; "))
        }

        val response = chain.proceed(requestBuilder.build())

        val responseCookies = response.headers("Set-Cookie")
        if (responseCookies.isNotEmpty()) {
            val existingCookies = sharedPreferences.getStringSet(PREF_COOKIES, mutableSetOf())
                ?.toMutableSet() ?: mutableSetOf()

            for (cookie in responseCookies) {
                val cookieName = cookie.substringBefore("=")
                existingCookies.removeAll { it.startsWith("$cookieName=") }
                existingCookies.add(cookie.substringBefore(";"))
            }

            sharedPreferences.edit()
                .putStringSet(PREF_COOKIES, existingCookies)
                .apply()
        }

        return response
    }

    fun clearCookies() {
        sharedPreferences.edit()
            .remove(PREF_COOKIES)
            .apply()
    }
}
