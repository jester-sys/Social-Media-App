package com.krishna.stitch.ActivityClass

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.CommentAdapter
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ActivityCommentBinding
import com.krishna.stitch.model.Comment
import com.krishna.stitch.model.Notification
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.Date

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    var postId: String? = null
    var postedBy: String? = null
    var list = ArrayList<Comment>()
    var database: FirebaseDatabase? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        postId = intent.getStringExtra("postId")
        postedBy = intent.getStringExtra("postedBy")

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID!!)
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)

                    if (!user?.profilePhoto.isNullOrEmpty()) {
                        Picasso.get()
                            .load(user?.profilePhoto)
                            .placeholder(R.drawable.kriana)
                            .into(binding.circleImageView)
                    } else {
                        binding.circleImageView.setImageResource(R.drawable.ic_message_icon)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        database!!.reference
            .child("posts")
            .child(postId!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val post: Post? = snapshot.getValue(Post::class.java)
                    val postImage = post?.postImage
                    if (postImage.isNullOrEmpty()) {
                        binding.shapeableImageView.visibility = View.GONE
                    } else {
                        binding.shapeableImageView.visibility = View.VISIBLE
                        Picasso.get()
                            .load(postImage)
                            .placeholder(R.drawable.kriana)
                            .into(binding.shapeableImageView)



                    }
                    binding.about.text = post?.postDescription
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })



        database!!.reference.child("Users")
            .child(postedBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)
                    Picasso.get()
                        .load(user!!.profilePhoto)
                        .placeholder(R.drawable.kriana)
                        .into(binding.profileImage)


                    binding.userName.text = user.name
                    binding.lastName.text = user.lastName
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        binding.commentPostBtn.setOnClickListener {

            val comment = Comment()
            comment.commentBody = binding.commentET.text.toString()
            comment.commentedAt = Date().time
            comment.commentedBy = FirebaseAuth.getInstance().uid!!

            database?.reference
                ?.child("posts")
                ?.child(postId!!)
                ?.child("comments")
                ?.push()
                ?.setValue(comment)
                ?.addOnSuccessListener {
                    database?.reference
                        ?.child("posts")
                        ?.child(postId!!)
                        ?.child("commentCount")
                        ?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var commentCount = 0
                                if (snapshot.exists()) {
                                    commentCount = snapshot.getValue(Int::class.java) ?: 0
                                }
                                database?.reference
                                    ?.child("posts")
                                    ?.child(postId!!)
                                    ?.child("commentCount")
                                    ?.setValue(commentCount + 1)
                                    ?.addOnSuccessListener {
                                        binding.commentET.setText("")
                                       var view:View
                                       Toast.makeText(this@CommentActivity, "Commented", Toast.LENGTH_SHORT).show()







                                        val notification = Notification()
                                        notification.notificationBy = FirebaseAuth.getInstance().uid
                                        notification.notificationAt = Date().time
                                        notification.postId = postId
                                        notification.postedBy = postedBy
                                        notification.type ="comment"

                                        FirebaseDatabase.getInstance().reference
                                            .child("notification")
                                            .child(postedBy!!)
                                            .push()
                                            .setValue(notification)

                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle onCancelled event
                            }
                        })
                }
        }
        val adapter = CommentAdapter(this, list)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        database!!.reference
            .child("posts")
            .child(postId!!)
            .child("comments").addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (dataSnapshot in snapshot.children) {
                        val comment = dataSnapshot.getValue(Comment::class.java)
                        list.add(comment!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })


    }
}
