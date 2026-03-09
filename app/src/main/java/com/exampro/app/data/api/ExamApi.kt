package com.exampro.app.data.api

import com.exampro.app.data.models.Exam
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExamApi {

    @GET("api/exams")
    suspend fun getExams(): Response<List<Exam>>

    @GET("api/exams/{id}")
    suspend fun getExam(@Path("id") id: Int): Response<Exam>
}
