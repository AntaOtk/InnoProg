package com.innoprog.android.network.data

import com.innoprog.android.feature.auth.authorization.data.AuthorizationBody
import com.innoprog.android.feature.auth.authorization.data.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    fun post(
        @Url url: String,
        @Body body: Any,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET
    fun get(@Url url: String, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @POST("/v1/login")
    suspend fun authorize(@Body body: AuthorizationBody): LoginResponse
}
