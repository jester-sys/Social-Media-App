package com.krishna.stitch.ProfileUI

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.CommentAdapter
import com.krishna.stitch.Adapter.PostAdapter
import com.krishna.stitch.R
import com.krishna.stitch.databinding.FragmentReplayBinding
import com.krishna.stitch.model.Comment
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.User


class ReplayFragment : Fragment() {
    private lateinit var binding: FragmentReplayBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: CommentAdapter // Update the adapter type to CommentAdapter
    private val commentList = ArrayList<Comment>() // Update the list type to Comment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReplayBinding.inflate(inflater, container, false)
        val rootView = binding.root

        recyclerView = binding.recyclerView

        // Configure RecyclerView
        postAdapter = CommentAdapter(requireActivity(), commentList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = postAdapter

        // Load posts and comments
        loadPostsAndComments()

        return rootView
    }

    private fun getCurrentUserId(): String {
        // Replace with your implementation to get the current user's ID
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    private fun loadPostsAndComments() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = ArrayList<Post>()
                commentList.clear()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let {
                        posts.add(it)

                        if (it.postedBy == getCurrentUserId()) {
                            val commentsSnapshot = postSnapshot.child("comments")
                            for (commentSnapshot in commentsSnapshot.children) {
                                val comment = commentSnapshot.getValue(Comment::class.java)
                                comment?.let {
                                    commentList.add(it)
                                }
                            }
                        }
                    }
                }

                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}

