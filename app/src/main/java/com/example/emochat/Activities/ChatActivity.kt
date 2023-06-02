package com.example.emochat.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.emochat.R
import com.example.emochat.databinding.ActivityChatBinding
import com.example.emochat.databinding.ActivityMainBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.backButton.setOnClickListener{
            finish()
        }
        binding.sendButton.setOnClickListener{
            Toast.makeText(this@ChatActivity, "Send button pressed", Toast.LENGTH_SHORT).show()
        }
    }
}