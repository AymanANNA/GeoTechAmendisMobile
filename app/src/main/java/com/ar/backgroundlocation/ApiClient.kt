package com.ar.backgroundlocation

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val device_name: String,
    val ip_address: String
)

data class LocationRequest(
    val technicien: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String // Add this line
)

data class TechnicienDetail(
    val technicien_id: Int,
    val device_name: String,
    val manufacturer: String,
    val os_version: String,
    val model: String,
    val ip_address: String,
    val battery_level: String,
    val last_sent_location_time: String
)

interface ApiService {
    @POST("technicien-login/")
    fun performLogin(@Body loginRequest: LoginRequest): Call<Map<String, Any>>

    @POST("sendlocalisations/")
    fun sendLocation(
        @Header("Authorization") authToken: String,
        @Body locationRequest: LocationRequest
    ): Call<Void>

    @POST("technicien/detail/")
    fun postTechnicienDetail(@Body technicienDetail: TechnicienDetail): Call<Unit>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.22:8000/api/"  // Update this line with your Ngrok URL
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
