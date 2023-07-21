package com.krishna.stitch.ChatActivity

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ChatRowBinding
import com.krishna.stitch.databinding.MessageRowBinding
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso



class MessageAdapter(private val context: Context) : RecyclerView.Adapter<MessageAdapter.MsgViewHolder>() {
    private val list: ArrayList<MessageModel> = ArrayList()

    inner class MsgViewHolder(val binding: MessageRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MessageRowBinding.inflate(inflater, parent, false)
        return MsgViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val model = list[position]

        if (model.senderId == FirebaseAuth.getInstance().uid) {
            holder.binding.leftChatView.visibility = View.GONE
            holder.binding.rightChatView.visibility = View.VISIBLE
            holder.binding.rightChatTextView.text = model.message
        } else {
            holder.binding.leftChatView.visibility = View.VISIBLE
            holder.binding.rightChatView.visibility = View.GONE
            holder.binding.leftChatTextView.text = model.message
        }
    }

    fun add(messageModel: MessageModel) {
        list.add(0, messageModel) // Add the message at the beginning of the list
        notifyItemInserted(0) // Notify adapter about the inserted item at position 0
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}



