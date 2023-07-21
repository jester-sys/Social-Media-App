package com.krishna.stitch.NotificationUi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.NotificationAdapter
import com.krishna.stitch.databinding.FragmentAllBinding
import com.krishna.stitch.model.Notification

class AllFragment : Fragment() {
    private lateinit var binding: FragmentAllBinding
    private var list: ArrayList<Notification>? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllBinding.inflate(layoutInflater)

        list = ArrayList()
        val adapter = NotificationAdapter(requireContext(), list!!)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.AllNotification.layoutManager = layoutManager
        binding.AllNotification.adapter = adapter

        database = FirebaseDatabase.getInstance()
        database!!.reference
            .child("notification")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list!!.clear()
                    for (dataSnapshot in snapshot.children) {
                        val notification = dataSnapshot.getValue(Notification::class.java)
                        notification?.notificationId = dataSnapshot.key
                        notification?.let { list!!.add(it) }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })

        return binding.root
    }
}
