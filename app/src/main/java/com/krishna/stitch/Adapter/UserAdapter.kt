package com.krishna.stitch.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.ActivityClass.FriendProfileActivity
import com.krishna.stitch.R
import com.krishna.stitch.databinding.UserSampleBinding
import com.krishna.stitch.model.Follow
import com.krishna.stitch.model.Notification
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.Date


class UserAdapter(private val context: Context, private var list: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: UserSampleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserSampleBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = list[position]

        if (user.profilePhoto.isNotEmpty()) {
            Picasso.get().load(user.profilePhoto).placeholder(R.drawable.kriana)
                .into(holder.binding.profileImage)
        } else {
            holder.binding.profileImage.setImageResource(R.drawable.kriana)
        }

        holder.binding.name.text = user.name
        holder.binding.UserName.text = user.username

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        FirebaseDatabase.getInstance().reference.child("Users").child(user.UserID!!)
            .child("followers").child(FirebaseAuth.getInstance().uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        holder.binding.followBtn.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                context, R.drawable.follow_active_btn
                            )
                        )
                        holder.binding.followBtn.text = "Following"
                        holder.binding.followBtn.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.grayTransparent
                            )
                        )
                    } else {
                        holder.binding.followBtn.setOnClickListener {
                            if (currentUserID != null) {
                                val currentUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID)

                                currentUserRef.child("following").child(user.UserID!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                // Already following, handle as desired
                                                Toast.makeText(context, "You are already following " + user.name, Toast.LENGTH_SHORT).show()
                                            } else {
                                                // Not following, proceed to follow
                                                val follow = Follow(
                                                    followedBy = currentUserID,
                                                    followedAt = Date().time
                                                )

                                                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(user.UserID!!)
                                                userRef.child("followers").child(currentUserID)
                                                    .setValue(follow).addOnSuccessListener {
                                                        userRef.child("followerCount").addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                val followerCount = snapshot.getValue(Int::class.java) ?: 0
                                                                userRef.child("followerCount").setValue(followerCount + 1)
                                                                    .addOnSuccessListener {
                                                                        // Increase the following count for the current user
                                                                        currentUserRef.child("followingCount")
                                                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                                    val currentFollowingCount = snapshot.getValue(Int::class.java) ?: 0
                                                                                    currentUserRef.child("followingCount")
                                                                                        .setValue(currentFollowingCount + 1)
                                                                                        .addOnSuccessListener {
                                                                                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn))
                                                                                            holder.binding.followBtn.text = "Following"
                                                                                            holder.binding.followBtn.setTextColor(ContextCompat.getColor(context, R.color.grayTransparent))
                                                                                            Toast.makeText(context, "You Followed " + user.name, Toast.LENGTH_SHORT).show()

                                                                                            val notification = Notification(
                                                                                                notificationBy = currentUserID,
                                                                                                notificationAt = Date().time,
                                                                                                type = "follow"
                                                                                            )

                                                                                            FirebaseDatabase.getInstance().reference
                                                                                                .child("notification")
                                                                                                .child(user.UserID!!)
                                                                                                .push()
                                                                                                .setValue(notification)
                                                                                        }
                                                                                }

                                                                                override fun onCancelled(error: DatabaseError) {
                                                                                    // Handle the error
                                                                                }
                                                                            })
                                                                    }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                // Handle the error
                                                            }
                                                        })
                                                    }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle the error
                                        }
                                    })
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })

        holder.itemView.setOnClickListener {
            // Get the clicked friend post
            val clickedFriendPost = list[position]
            val intent = Intent(context, FriendProfileActivity::class.java)
            intent.putExtra("Name", clickedFriendPost.name)
            intent.putExtra("lastName", clickedFriendPost.lastName)
            intent.putExtra("Profession", clickedFriendPost.profession)
            intent.putExtra("Bio", clickedFriendPost.bio)
            intent.putExtra("UserName", clickedFriendPost.username)
            intent.putExtra("ImageURL", clickedFriendPost.profilePhoto)
            intent.putExtra("followers", "${clickedFriendPost.followerCount} Followers")
            intent.putExtra("Posts", "${clickedFriendPost.postCount} Posts")
            intent.putExtra("Following", "${clickedFriendPost.followingCount} Following")

            context.startActivity(intent)
        }


    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<User>) {
        list = filteredList
        notifyDataSetChanged()
    }
}

