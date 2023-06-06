package com.example.emochat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emochat.Activities.LoginActivity
import com.example.emochat.Adapters.UserAdapter
import com.example.emochat.Models.User
import com.example.emochat.PreferenceHelper.Helper
import com.example.emochat.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: Helper
    private lateinit var binding: ActivityMainBinding
    private lateinit var recycleView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recycleView = binding.recyclerView
        sharedPref = Helper(this)
        setListeners()

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
    private fun setListeners(){
        var isSearchBoxEmpty = true
        binding.buttonLogout.setOnClickListener{
            sharedPref.clear()
            Toast.makeText(this@MainActivity, "Logout successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.clearIcon.setOnClickListener{
            binding.searchQuery.text?.clear()
        }
        binding.searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                isSearchBoxEmpty = s.isNullOrEmpty()
                if(isSearchBoxEmpty){
                    binding.searchIcon.visibility = View.VISIBLE
                    binding.clearIcon.visibility = View.GONE
                }
                else{
                    binding.searchIcon.visibility = View.GONE
                    binding.clearIcon.visibility = View.VISIBLE
                }
                val query = s.toString()
                search(query)
            }
        })
        //clearing input layout focus
        binding.searchQuery.setOnEditorActionListener{_, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchQuery.windowToken, 0)  // Hide the keyboard
                binding.textInputLayout.clearFocus()  // Remove focus from the TextInputLayout
                true
            } else {
                false
            }
        }
    }
    private suspend fun parseUserListFromJson(): List<User> = withContext(Dispatchers.IO) {
        val inputStream = resources.openRawResource(R.raw.user_data)
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
    private fun search(query: String) {
        val adapter = recycleView.adapter as? UserAdapter
        adapter?.search(query)
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