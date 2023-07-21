package com.krishna.stitch.ProfileUI

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.PostAdapter
import com.krishna.stitch.R
import com.krishna.stitch.databinding.FragmentStitcherBinding
import com.krishna.stitch.model.Post



class StitcherFragment : Fragment() {

    private lateinit var binding: FragmentStitcherBinding
    private lateinit var postAdapter: PostAdapter
    private val postList: ArrayList<Post> = ArrayList()
    private var database: FirebaseDatabase? = null
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance()
        binding = FragmentStitcherBinding.inflate(layoutInflater)
        postAdapter = PostAdapter(postList, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = postAdapter

        currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        // Query the posts for the current user
        database?.reference?.child("posts")?.orderByChild("postedBy")?.equalTo(currentUserID)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val post = dataSnapshot.getValue(Post::class.java)
                        post?.postId = dataSnapshot.key
                        post?.let { postList.add(it) }
                    }
                    postAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        return binding.root
    }
}

