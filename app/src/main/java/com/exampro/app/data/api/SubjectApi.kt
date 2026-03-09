package com.exampro.app.data.api

import com.exampro.app.data.models.Subject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SubjectApi {

    @GET("api/subjects")
    suspend fun getSubjects(): Response<List<Subject>>

    @GET("api/subjects/{id}")
    suspend fun getSubject(@Path("id") id: Int): Response<Subject>
}
