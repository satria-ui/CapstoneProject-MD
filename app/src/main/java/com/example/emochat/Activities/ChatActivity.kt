package com.example.emochat.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emochat.Adapters.ChatAdapter
import com.example.emochat.Models.ChatMessage
import com.example.emochat.R
import com.example.emochat.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var recycleView: RecyclerView
    private lateinit var mediaRecorder: MediaRecorder
    private val chatList = mutableListOf<ChatMessage>()
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1
    private var audioFilePath = ""
    private var currentUser = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textName.text = intent.getStringExtra("userName")
        recycleView = binding.recyclerView
        setListeners()

        loading(true)
        enableChatBox(false)
        enableRecord(false)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            chatList.addAll(withContext(Dispatchers.IO) {
                parseChatDataFromJson()
            })
            loading(false)
            showRecyclerList(chatList)
            enableChatBox(true)
            enableRecord(true)
            setKeyboardListener()
        }
    }

    private fun setListeners(){
        var isChatBoxEmpty = true
        var isRecording = false

        binding.backButton.setOnClickListener{
            finish()
        }
        binding.sendButton.setOnClickListener{
            val message = binding.chatBox.text.toString().trim()
            if(message.isNotEmpty()){
                val chatMessage = ChatMessage("Me", message, currentUser, generateTimeStamp())
                chatList.add(chatMessage)
                binding.chatBox.text.clear()
                recycleView.adapter?.notifyItemInserted(chatList.size - 1)
                recycleView.scrollToPosition(chatList.size - 1)
            }
            else if(message.isEmpty()){
                when(isRecording){
                    false -> {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            startRecording()
                        } else {
                            requestAudioPermission()
                        }
                        binding.recordIcon.visibility = View.GONE
                        binding.stopRecordIcon.visibility = View.VISIBLE
                        binding.sendButton.setBackgroundResource(R.drawable.background_recording_button)
                        enableChatBox(false)
                        isRecording = true
                    }
                    true->{
                        binding.recordIcon.visibility = View.VISIBLE
                        binding.stopRecordIcon.visibility = View.GONE
                        binding.sendButton.setBackgroundResource(R.drawable.selector_send_button)
                        enableChatBox(true)
                        isRecording = false
                        stopRecording()
                    }
                }
            }
        }
        binding.chatBox.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                isChatBoxEmpty = text?.trim().isNullOrEmpty()
                if(isChatBoxEmpty){
                    binding.recordIcon.visibility = View.VISIBLE
                    binding.sendIcon.visibility = View.GONE
                }
                else{
                    binding.recordIcon.visibility = View.GONE
                    binding.sendIcon.visibility = View.VISIBLE
                }
            }
        })
        binding.sendButton.setOnLongClickListener{
            if(isChatBoxEmpty){
                currentUser = when(currentUser){
                    true -> false
                    false -> true
                }
                Toast.makeText(this@ChatActivity, "User changed", Toast.LENGTH_SHORT).show()
                true
            }
            else{
                false
            }
        }
    }
    private fun showRecyclerList(chatList: List<ChatMessage>){
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = ChatAdapter(chatList)
        recycleView.scrollToPosition(recycleView.adapter!!.itemCount-1)
    }

    private suspend fun parseChatDataFromJson(): List<ChatMessage> = withContext(Dispatchers.IO) {
        val inputStream = resources.openRawResource(R.raw.chat_data)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val chatList = mutableListOf<ChatMessage>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val user = jsonObject.getString("user")
            val message = jsonObject.getString("message")
            val isSent = jsonObject.getBoolean("isSent")
            val time = generateTimeStamp()
            val chatData = ChatMessage(user, message, isSent, time)
            Log.d("JSON DATA", user)
            Log.d("JSON DATA", message)
            Log.d("JSON DATA", isSent.toString())
            Log.d("JSON DATA", time)
            chatList.add(chatData)
        }
        chatList
    }
    private fun generateTimeStamp(): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val currentTime = LocalTime.now()
        return currentTime.format(formatter)
    }
    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
        else{
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
    private fun enableChatBox(enabled: Boolean){
        binding.chatBox.isEnabled = enabled
    }
    private fun enableRecord(enabled: Boolean){
        binding.sendButton.isEnabled = enabled
    }
    private fun setKeyboardListener() {
        var isKeyboardOpen = false
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            val keyboardOpenThreshold = resources.displayMetrics.heightPixels / 4

            if (heightDiff > keyboardOpenThreshold) {
                // Keyboard is open
                if (!isKeyboardOpen) {
                    isKeyboardOpen = true
                    recycleView.post {
                        recycleView.scrollToPosition(recycleView.adapter!!.itemCount - 1)
                    }
                }
            } else {
                // Keyboard is closed
                if (isKeyboardOpen) {
                    isKeyboardOpen = false
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    private fun startRecording(){
        mediaRecorder = MediaRecorder()

        // Set the audio source and output format
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

        // Set the output file path
        audioFilePath = getUniqueAudioFilePath()
        mediaRecorder.setOutputFile(audioFilePath)

        // Set the audio encoder
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            // Prepare and start the recording
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun stopRecording(){
        mediaRecorder.apply {
            try {
                // Stop the recording
                stop()
                reset()
                release()

                val audioUri: String = Uri.fromFile(File(audioFilePath)).toString()
                val audioMessage = ChatMessage(user = "Me", message = "", isSent = currentUser, isAudioMessage = true, audioUri = audioUri, time = generateTimeStamp())
                chatList.add(audioMessage)
                recycleView.adapter?.notifyItemInserted(chatList.size - 1)
                recycleView.scrollToPosition(chatList.size - 1)
                Log.d("plerplertaikuda", audioUri)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getUniqueAudioFilePath(): String {
        val baseDir = externalCacheDir?.absolutePath ?: ""
        val fileName = "recording_${System.currentTimeMillis()}.3gp"
        return "$baseDir/$fileName"
    }
    private fun deleteAllRecordedFiles() {
        val baseDir = externalCacheDir?.absolutePath ?: ""
        val directory = File(baseDir)
        val files = directory.listFiles { _, name -> name.startsWith("recording_") }
        files?.forEach { it.delete() }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteAllRecordedFiles()
    }
    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed with your recording logic
                startRecording()
            } else {
                // Permission is denied, handle the case accordingly (e.g., show a message)
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}