package com.example.emochat.Activities

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
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.emochat.MainActivity
import com.example.emochat.R
import com.example.emochat.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            //testing vibration
            colorTransition()
            vibrate()
            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            //login logic
            if(isValidLogin()){
                login()
            }
        }
    }
    private fun login(){
        loading(true)
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            loading(false)
            Toast.makeText(applicationContext, "Login success", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun isValidLogin(): Boolean{
        var isValid = true

        if(binding.inputEmail.text.toString().trim().isEmpty()){
            binding.inputEmail.error = "Please enter email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            binding.inputEmail.error="Enter a valid email address"
            isValid = false
        }
        if(binding.inputPassword.text.toString().trim().isEmpty()){
            binding.inputPassword.error = "Please enter password"
            isValid = false
        }
        return isValid
    }
    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.buttonLogin.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonLogin.visibility = View.VISIBLE
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