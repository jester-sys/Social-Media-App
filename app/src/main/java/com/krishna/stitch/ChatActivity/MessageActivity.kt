package com.krishna.stitch.ChatActivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ActivityMessageBinding
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.UUID


class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var databaseSender: DatabaseReference
    private lateinit var databaseReceiver: DatabaseReference

    private var sendRoom: String? = null
    private var receiverRoom: String? = null
    private var adapter: MessageAdapter? = null
    private lateinit var onlineStatusListener: ValueEventListener

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MessageAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        val receiverId = intent.getStringExtra("id")
        var name = intent.getStringExtra("name")
        var lastname = intent.getStringExtra("lastname")
        var profilePhoto = intent.getStringExtra("ProfileImage")

        binding.name.text = "$name $lastname"
        Picasso.get().load(profilePhoto).into(binding.profileImage)

        sendRoom = FirebaseAuth.getInstance().uid + receiverId
        receiverRoom = receiverId + FirebaseAuth.getInstance().uid

        databaseSender = FirebaseDatabase.getInstance().getReference("chats").child(sendRoom!!)
        databaseReceiver = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom!!)

        databaseSender.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter?.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val messageModel = snapshot.getValue(MessageModel::class.java)
                    messageModel?.let { adapter?.add(it) }
                }
                binding.recyclerView.scrollToPosition(adapter?.itemCount?.minus(1) ?: 0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error here if needed
            }
        })

        binding.commentPostBtn.setOnClickListener {
            val message = binding.commentET.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }

        // Add online status listener
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID!!)
        onlineStatusListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)
                    val isOnline = user?.onlineStatus ?: false
                    updateUserOnlineStatus(isOnline)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        }
        currentUserRef.addValueEventListener(onlineStatusListener)
    }

    private fun sendMessage(message: String) {
        val messageId = UUID.randomUUID().toString()
        val senderId = FirebaseAuth.getInstance().uid
        val messageModel = MessageModel(messageId, senderId, message)

        adapter?.add(messageModel)
        binding.recyclerView.scrollToPosition(adapter?.itemCount?.minus(1) ?: 0)

        databaseSender.child(messageId).setValue(messageModel)
        databaseReceiver.child(messageId).setValue(messageModel)

        binding.commentET.text = null
    }


    @SuppressLint("SetTextI18n")
    private fun updateUserOnlineStatus(isOnline: Boolean) {
        val statusImageView: TextView = findViewById(R.id.statusImageView)
        if (isOnline) {

            statusImageView.text = "online"
        } else {
           statusImageView.text = "offline"
        }
    }

    override fun onPause() {
        super.onPause()

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID!!)
        currentUserRef.removeEventListener(onlineStatusListener)
    }
}