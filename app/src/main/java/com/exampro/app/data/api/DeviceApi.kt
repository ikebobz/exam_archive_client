package com.exampro.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class DeviceRegistrationRequest(
    @SerializedName("deviceToken") val token: String,
    val platform: String = "android"
)

interface DeviceApi {
    @POST("api/devices/register")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<Unit>
}
