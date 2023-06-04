package com.example.emochat.Models

import java.io.Serializable

data class ChatMessage(val user: String, val message: String, val isSent: Boolean, val time:String): Serializable