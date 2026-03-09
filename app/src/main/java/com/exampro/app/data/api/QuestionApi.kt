package com.exampro.app.data.api

import com.exampro.app.data.models.Question
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuestionApi {

    @GET("api/questions")
    suspend fun getQuestions(): Response<List<Question>>

    @GET("api/questions/{id}")
    suspend fun getQuestion(@Path("id") id: Int): Response<Question>
}
