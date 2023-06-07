package com.example.emochat.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.emochat.Activities.ChatActivity
import com.example.emochat.Models.User
import com.example.emochat.databinding.ItemUserBinding

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){
    private var filteredUserList: List<User> = userList
    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.textName.text = user.name
            Glide.with(itemView.context)
                .load(user.imageUri)
                .circleCrop()
                .into(binding.imageProfile)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("userName", user.name)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredUserList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredUserList[position]
        holder.bind(user)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun search(query: String) {
        filteredUserList = if (query.isNotEmpty()) {
            userList.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            userList
        }
        notifyDataSetChanged()
    }
}