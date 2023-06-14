package com.example.emochat.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emochat.Models.ChatMessage
import com.example.emochat.R
import com.example.emochat.databinding.ItemAudioLeftBinding
import com.example.emochat.databinding.ItemAudioRightBinding
import com.example.emochat.databinding.ItemReceiveMessageBinding
import com.example.emochat.databinding.ItemSentMessageBinding
import java.lang.IllegalArgumentException

class ChatAdapter(private val chatList: List<ChatMessage>, private val isAudioChangedListener: (Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var isAudio: Boolean = false

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
    inner class VoiceMessageSentViewHolder(private val binding: ItemAudioRightBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: ChatMessage){
            binding.textDateTime.text = message.time
            binding.playAudio.setAudio(message.audioUri)
        }
    }
    inner class VoiceMessageReceiveViewHolder(private val binding: ItemAudioLeftBinding): RecyclerView.ViewHolder(binding.root){
//        init {
//            binding.root.addOnAttachStateChangeListener(this)
//        }
        fun bind(message: ChatMessage){
            binding.textDateTime.text = message.time
            binding.playAudio.setAudio(message.audioUri)
        }
//        override fun onViewAttachedToWindow(p0: View) {}
//        override fun onViewDetachedFromWindow(p0: View) {
//            binding.playAudio.onStop()
//        }
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
            VIEW_TYPE_VOICE_SENT ->{
                isAudio = true
                isAudioChangedListener.invoke(isAudio)
                val binding = ItemAudioRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VoiceMessageSentViewHolder(binding)
            }
            VIEW_TYPE_VOICE_RECEIVED ->{
                val binding = ItemAudioLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VoiceMessageReceiveViewHolder(binding)
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
            is VoiceMessageSentViewHolder -> holder.bind(message)
            is VoiceMessageReceiveViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatList[position]
        return if (message.isSent) {
            if (message.isAudioMessage) {
                VIEW_TYPE_VOICE_SENT
            } else {
                VIEW_TYPE_SENT
            }
        }
        else {
            if (message.isAudioMessage) {
                VIEW_TYPE_VOICE_RECEIVED
            } else {
                VIEW_TYPE_RECEIVED
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_RECEIVED = 0
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_VOICE_SENT = 2
        private const val VIEW_TYPE_VOICE_RECEIVED = 3
    }
}