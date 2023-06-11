package com.example.emochat.Activities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.emochat.MainActivity
import com.example.emochat.Network.RegisterResponse
import com.example.emochat.Network.RetrofitClient
import com.example.emochat.PreferenceHelper.Helper
import com.example.emochat.Utils.Constants
import com.example.emochat.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var sharedPref: Helper
    private var encodedImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = Helper(this)
        setListeners()
    }

    private fun setListeners(){
        binding.textLogin.setOnClickListener { finish() }
        binding.buttonRegister.setOnClickListener {
            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            //register logic
            if(isValidRegister()){
                val email = binding.inputEmail.text.toString()
                val password = binding.inputPassword.text.toString()
                register(email, password)
            }
        }
        binding.layoutImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
            binding.addImage.error = null
        }
    }
    private fun register(email:String, password:String){
        loading(true)
        RetrofitClient.apiService.register(email, password).enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    val accessToken = response.body()?.accessToken
                    Toast.makeText(this@RegisterActivity, "Register success", Toast.LENGTH_SHORT).show()
                    Log.d("Register", "Response is successful. Access Token: $accessToken")
                    if(accessToken!=null){
                        sharedPref.put(Constants.PREF_TOKEN, accessToken)
                        sharedPref.put(Constants.PREF_IS_LOGIN, true)
                        sharedPref.put(Constants.PREF_EMAIL, email)

                        // Start MainActivity
                        loading(false)
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    try{
                        val errorBody = response.errorBody()?.string()
                        val errorJson = JSONObject(errorBody)
                        val statusCode = errorJson.getInt("statusCode")
                        val message = errorJson.getString("message")
                        Toast.makeText(this@RegisterActivity, "Failed to register: $message", Toast.LENGTH_SHORT).show()
                        Log.e("Register", "Failed to register. Status Code: $statusCode, Message: $message")
                    }
                    catch (e: JSONException){
                        Toast.makeText(this@RegisterActivity, "Failed to parse error response", Toast.LENGTH_SHORT).show()
                        Log.e("Register", "Failed to parse error response: ${e.message}")
                    }
                    loading(false)
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Failed to register: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Error: ${t.message}")
                loading(false)
            }

        })
    }

    private fun encodedImage(bitmap:Bitmap): String{
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitMap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
             if(data != null){
                 val imageUri: Uri? = data.data
                 try{
                     val inputStream = contentResolver.openInputStream(imageUri!!)
                     val bitmap = BitmapFactory.decodeStream(inputStream)
                     binding.imageProfile.setImageBitmap(bitmap)
                     binding.textAddImage.visibility = View.GONE
                     encodedImage = encodedImage(bitmap)
                 }
                 catch(e: FileNotFoundException){
                     e.printStackTrace()
                 }
             }
        }
    }
    private fun isValidRegister(): Boolean {
        var isValid = true
        binding.inputPassword.error = null
        binding.inputConfirmPassword.error = null
        binding.addImage.error = null

        if (encodedImage.isEmpty()){
            binding.addImage.visibility = View.VISIBLE
            binding.addImage.error = "Select profile image"
            isValid = false
        }
        if(binding.inputName.text.toString().trim().isEmpty()){
            binding.inputName.error = "Please enter name"
            isValid = false
        }
        if(binding.inputEmail.text.toString().trim().isEmpty()){
            binding.inputEmail.error = "Please enter email"
            isValid = false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            binding.inputEmail.error = "Enter a valid email address"
            isValid = false
        }
        if(binding.inputPassword.text.toString().trim().isEmpty()){
            binding.inputPassword.error = "Please enter password"
            isValid = false
        }
        if(binding.inputConfirmPassword.text.toString().trim().isEmpty()){
            binding.inputConfirmPassword.error = "Please confirm your password"
            isValid = false
        }
        if(binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()) {
            binding.inputPassword.error = "Password & confirm password must be the same"
            binding.inputConfirmPassword.error = "Password & confirm password must be the same"
            isValid = false
        }
            return isValid
    }
    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.buttonRegister.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonRegister.visibility = View.VISIBLE
        }
    }
}