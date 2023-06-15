package com.example.emochat.Activities

import android.content.ContentValues
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
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.emochat.MainActivity
import com.example.emochat.Network.LoginResponse
import com.example.emochat.Network.RetrofitClient
import com.example.emochat.PreferenceHelper.Helper
import com.example.emochat.R
import com.example.emochat.Utils.Constants
import com.example.emochat.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var sharedPref: Helper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = Helper(this)
        setListeners()
    }

    private fun setListeners(){
        binding.textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener{
            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            //login logic
            if(isValidLogin()){
                val email = binding.inputEmail.text.toString()
                val password = binding.inputPassword.text.toString()
                login(email, password)
            }
        }
    }
    override fun onStart(){
        super.onStart()
        if(sharedPref.getBoolean(Constants.PREF_IS_LOGIN)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun login(email:String, password:String){
        loading(true)
        RetrofitClient.apiService.login(email, password).enqueue(object:Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val accessToken = response.body()?.accessToken
                    Log.d("LOGIN", "Response is successful. Access Token: $accessToken")
                    if(accessToken!=null){
                        sharedPref.put(Constants.PREF_TOKEN, accessToken)
                        sharedPref.put(Constants.PREF_IS_LOGIN, true)
                        sharedPref.put(Constants.PREF_EMAIL, email)

                        // Start MainActivity
                        loading(false)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity, "Invalid response", Toast.LENGTH_SHORT).show()
                        loading(false)
                    }
                }
                else{
                    Toast.makeText(this@LoginActivity, "Failed to Login", Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Failed to login: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Error: ${t.message}")
                loading(false)
            }
        })
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
        vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    @Suppress("DEPRECATION")
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