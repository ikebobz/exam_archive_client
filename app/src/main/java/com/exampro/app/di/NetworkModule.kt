package com.exampro.app.di

import android.content.Context
import android.content.SharedPreferences
import com.exampro.app.data.api.AuthApi
import com.exampro.app.data.api.DashboardApi
import com.exampro.app.data.api.DynamicBaseUrlInterceptor
import com.exampro.app.data.api.ExamApi
import com.exampro.app.data.api.QuestionApi
import com.exampro.app.data.api.SessionCookieInterceptor
import com.exampro.app.data.api.SubjectApi
import com.exampro.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CookiePrefs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthPrefs

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @CookiePrefs
    @Provides
    @Singleton
    fun provideCookiePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.COOKIE_PREFS_NAME, Context.MODE_PRIVATE)
    }

    @AuthPrefs
    @Provides
    @Singleton
    fun provideAuthPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSessionCookieInterceptor(@CookiePrefs prefs: SharedPreferences): SessionCookieInterceptor {
        return SessionCookieInterceptor(prefs)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionCookieInterceptor: SessionCookieInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(dynamicBaseUrlInterceptor)
            .addInterceptor(sessionCookieInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExamApi(retrofit: Retrofit): ExamApi {
        return retrofit.create(ExamApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSubjectApi(retrofit: Retrofit): SubjectApi {
        return retrofit.create(SubjectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionApi(retrofit: Retrofit): QuestionApi {
        return retrofit.create(QuestionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardApi(retrofit: Retrofit): DashboardApi {
        return retrofit.create(DashboardApi::class.java)
    }
}
