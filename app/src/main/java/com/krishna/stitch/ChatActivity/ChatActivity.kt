package com.krishna.stitch.ChatActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.UserAdapter
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ActivityChatBinding
import com.krishna.stitch.model.User


class ChatActivity : AppCompatActivity() {

    lateinit var binding:ActivityChatBinding
    private var database: FirebaseDatabase? = null
    private val list = ArrayList<User>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        adapter = ChatAdapter(this, list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val usersRef = database?.getReference()?.child("Users")
        usersRef?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val currentUserID = FirebaseAuth.getInstance().uid

                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.UserID = dataSnapshot.key

                    if (user?.UserID != currentUserID) {
                        user?.let { list.add(it) }
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })


    }

    private fun filter(query: String?) {
        val filteredList = ArrayList<User>()

        if (!query.isNullOrEmpty()) {
            for (user in list) {
                val fullName = user.name + " " + user.lastName
                if (fullName.contains(query, ignoreCase = true)) {
                    filteredList.add(user)
                }
            }
        } else {
            filteredList.addAll(list)
        }

        adapter?.filterList(filteredList)
    }

}