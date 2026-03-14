package com.exampro.app.data.api

import com.exampro.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicBaseUrlInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        val newBaseUrl = runBlocking { settingsRepository.baseUrl.first() }
        val newHttpUrl = newBaseUrl.toHttpUrlOrNull()
        
        if (newHttpUrl != null) {
            val newUrl = request.url.newBuilder()
                .scheme(newHttpUrl.scheme)
                .host(newHttpUrl.host)
                .port(newHttpUrl.port)
                .build()
            request = request.newBuilder()
                .url(newUrl)
                .build()
        }
        
        return chain.proceed(request)
    }
}
