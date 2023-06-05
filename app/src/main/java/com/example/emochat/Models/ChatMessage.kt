package com.example.emochat.Models

import android.net.Uri
import java.io.Serializable

data class ChatMessage(
    val user: String,
    val message: String,
    val isSent: Boolean,
    val time:String,
    val isAudioMessage: Boolean = false,
    val audioUri: String = "") : Serializable