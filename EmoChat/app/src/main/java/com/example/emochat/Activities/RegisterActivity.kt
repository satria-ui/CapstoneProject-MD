package com.example.emochat.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.emochat.MainActivity
import com.example.emochat.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var encodedImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners(){
        binding.textLogin.setOnClickListener { finish() }
        binding.buttonRegister.setOnClickListener {
            if(isValidSignUp()){
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    private fun signUp(){
        loading(true)
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            loading(false)
            showToast("Register complete.")

            finish()
        }
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
    private fun isValidSignUp(): Boolean {
        if (encodedImage.isEmpty()){
            showToast("Select profile image")
            return false
        }
        else if(binding.inputName.text.toString().trim().isEmpty()){
            showToast("Please enter name")
            return false
        }
        else if(binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Please enter email")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Enter a valid email address")
            return false
        }
        else if(binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter password")
            return false
        }
        else if(binding.inputConfirmPassword.text.toString().trim().isEmpty()){
            showToast("Confirm your password")
            return false
        }
        else if(binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()){
            showToast("Password & confirm password must be the same")
            return false
        }
        else{
            return true
        }
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