package com.example.emochat.Activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.emochat.R
import com.example.emochat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener{
            colorTransition()
            vibrate()
            Toast.makeText(applicationContext, "Clicked Button", Toast.LENGTH_SHORT).show()
        }
    }
    @Suppress("DEPRECATION")
    private fun vibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(Build.VERSION.SDK_INT >= 26){
            vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else{
            vibrator.vibrate(1500)
        }
    }

    private fun colorTransition(){
        binding.scrollView.setBackgroundColor(Color.RED)
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val redColor = ContextCompat.getColor(this, R.color.angry_transition)
        val transitionDrawable = TransitionDrawable(arrayOf(ColorDrawable(primaryColor), ColorDrawable(redColor)))

        // Set the TransitionDrawable as the background of the ScrollView
        binding.scrollView.background = transitionDrawable
        // Start the transition animation
        transitionDrawable.startTransition(1500) // 2 seconds duration
        // Use Handler to delay reverting the background color
        val handler = Handler()
        handler.postDelayed({
            // Reverse the transition animation
            transitionDrawable.reverseTransition(1500) // 2 seconds duration
        }, 1500) // 2 seconds delay before reversing the transition
    }
}