package com.example.emochat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emochat.Models.ChatMessage
import com.example.emochat.databinding.ItemReceiveMessageBinding
import com.example.emochat.databinding.ItemSentMessageBinding
import java.lang.IllegalArgumentException

class ChatAdapter(private val chatList: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class ReceivedMessageViewHolder(private val binding: ItemReceiveMessageBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(message: ChatMessage){
            binding.textMessage.text = message.message
            binding.textDateTime.text = message.time
        }

    }
    inner class SentMessageViewHolder(private val binding: ItemSentMessageBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(message:ChatMessage){
            binding.textMessage.text = message.message
            binding.textDateTime.text = message.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemReceiveMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReceivedMessageViewHolder(binding)
            }
            VIEW_TYPE_SENT -> {
                val binding = ItemSentMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SentMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatList[position]
        when(holder){
            is ReceivedMessageViewHolder -> holder.bind(message)
            is SentMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatList[position]
        return if (message.isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    companion object {
        private const val VIEW_TYPE_RECEIVED = 0
        private const val VIEW_TYPE_SENT = 1
    }
}