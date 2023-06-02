package com.example.emochat.Network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServices {
    @POST("auth/login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("auth/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>
}