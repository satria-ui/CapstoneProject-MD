package com.example.emochat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
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
import okhttp3.Dispatcher
import org.json.JSONArray
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var recycleView: RecyclerView
    private val chatList = mutableListOf<ChatMessage>()
    private var isKeyboardOpen = false
    private var currentUser = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textName.text = intent.getStringExtra("userName")
        recycleView = binding.recyclerView
        setListeners()
        setKeyboardListener()

        loading(true)
        enableChatBox(false)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            chatList.addAll(withContext(Dispatchers.IO) {
                parseChatDataFromJson()
            })
//            val chatList = withContext(Dispatchers.IO) {
//                parseChatDataFromJson()
//            }
            loading(false)
            showRecyclerList(chatList)
            enableChatBox(true)
        }
    }

    private fun setListeners(){
        var isChatBoxEmpty = true

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
        }
        binding.chatBox.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                isChatBoxEmpty = text.isNullOrEmpty()
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
        binding.chatBox.isFocusable = enabled
        binding.chatBox.isFocusableInTouchMode = enabled
    }
    private fun setKeyboardListener() {
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
}