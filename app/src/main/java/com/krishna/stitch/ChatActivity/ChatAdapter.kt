package com.krishna.stitch.ChatActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ChatRowBinding
import com.krishna.stitch.databinding.DashboardRvBinding
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso


//class ChatAdapter(private val context: Context, private var list: ArrayList<User>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
//
//    inner class ChatViewHolder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ChatRowBinding.inflate(inflater, parent, false)
//        return ChatViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
//        val model = list[position]
//        holder.binding.name.text = HtmlCompat.fromHtml("<b>${model.name}&nbsp;${model.lastName}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
//
//        if (!model.profilePhoto.isNullOrEmpty()) {
//            Picasso.get()
//                .load(model.profilePhoto)
//                .placeholder(R.drawable.kriana)
//                .into(holder.binding.profileImage)
//        } else {
//            // Handle empty or null profile photo case here
//            Picasso.get()
//                .load(R.drawable.kriana)
//                .placeholder(R.drawable.kriana)
//                .into(holder.binding.profileImage)
//        }
//        holder.itemView.setOnClickListener {
//            var intent = Intent(context,MessageActivity::class.java)
//            intent.putExtra("id",model.UserID)
//            intent.putExtra("name",model.name)
//            intent.putExtra("lastname",model.lastName)
//            intent.putExtra("ProfileImage",model.profilePhoto)
//            context.startActivity(intent)
//        }
//    }
//    @SuppressLint("NotifyDataSetChanged")
//    fun filterList(filteredList: ArrayList<User>) {
//        list = filteredList
//        notifyDataSetChanged()
//    }
//}

class ChatAdapter(private val context: Context, private var list: ArrayList<User>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatRowBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val model = list[position]
        holder.binding.name.text = HtmlCompat.fromHtml("<b>${model.name}&nbsp;${model.lastName}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Check the hasNewMessage status and apply highlight if it's true
        if (model.hasNewMessage) {
            // Apply highlight to the row
            holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        } else {
            // Remove highlight from the row
            holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

//        holder.binding.latestMessage.text = model.latestMessage
        if (!model.profilePhoto.isNullOrEmpty()) {
            Picasso.get()
                .load(model.profilePhoto)
                .placeholder(R.drawable.kriana)
                .into(holder.binding.profileImage)
        } else {
            // Handle empty or null profile photo case here
            Picasso.get()
                .load(R.drawable.kriana)
                .placeholder(R.drawable.kriana)
                .into(holder.binding.profileImage)
        }
        holder.itemView.setOnClickListener {
            // Set hasNewMessage to false when the user clicks on the row to open the message activity
            model.hasNewMessage = false
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(model.UserID!!)
            userRef.child("hasNewMessage").setValue(false)

            var intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("id", model.UserID)
            intent.putExtra("name", model.name)
            intent.putExtra("lastname", model.lastName)
            intent.putExtra("ProfileImage", model.profilePhoto)
            context.startActivity(intent)
        }
    }

    // Function to update the list with new messages and highlight the row if needed
    @SuppressLint("NotifyDataSetChanged")
    fun updateListWithMessages(newMessages: ArrayList<User>) {
        for (newMessage in newMessages) {
            val user = list.find { it.UserID == newMessage.UserID }
            if (user != null) {
                // Compare the latest message timestamp with the timestamp of the last message received
                if (user.latestMessageTimestamp < newMessage.latestMessageTimestamp) {
                    user.latestMessage = newMessage.latestMessage // Update the latestMessage field
                    user.latestMessageTimestamp = newMessage.latestMessageTimestamp
                    user.hasNewMessage = true
                }
            }
        }
        notifyDataSetChanged() // Notify the adapter about the changes
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<User>) {
        list = filteredList
        notifyDataSetChanged()
    }

}

