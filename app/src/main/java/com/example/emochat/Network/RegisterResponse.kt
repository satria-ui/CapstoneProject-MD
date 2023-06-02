package com.example.emochat.Network

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("access_token")
    val accessToken: String
)