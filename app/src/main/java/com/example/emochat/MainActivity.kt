package com.example.emochat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emochat.Adapters.UserAdapter
import com.example.emochat.Models.User
import com.example.emochat.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recycleView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recycleView = binding.recyclerView

        loading(true)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            val userList = withContext(Dispatchers.IO) {
                parseUserListFromJson()
            }
            loading(false)
            showRecyclerList(userList)
        }
    }

    private fun showRecyclerList(userList: List<User>){
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = UserAdapter(userList)
    }
    private suspend fun parseUserListFromJson(): List<User> = withContext(Dispatchers.IO) {
        val inputStream = resources.openRawResource(R.raw.dummy)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val userList = mutableListOf<User>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val imageUri = jsonObject.getString("imageUri")
            val user = User(name, imageUri)
            Log.d("JSON", name)
            Log.d("JSON", imageUri)
            userList.add(user)
        }
        userList
    }
    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        }
        else{
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}