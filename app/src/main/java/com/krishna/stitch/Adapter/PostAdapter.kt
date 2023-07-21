package com.krishna.stitch.Adapter

import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.ActivityClass.CommentActivity
import com.krishna.stitch.ActivityClass.FriendProfileActivity
import com.krishna.stitch.R
import com.krishna.stitch.databinding.DashboardRvBinding
import com.krishna.stitch.model.Notification
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.Date
import java.util.regex.Pattern


class PostAdapter(private val list: ArrayList<Post>, private val context: Context) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DashboardRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DashboardRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        Picasso.get()
            .load(model.postImage)
            .placeholder(R.drawable.kriana)
            .into(holder.binding.shapeableImageView)

        holder.binding.Liketext.text = "${model.postLike} likes"
        holder.binding.CommentText.text = "${model.commentCount} reply"

        val description = model.postDescription
        if (description.isNullOrEmpty()) {
            holder.binding.about.visibility = View.GONE
        } else {
            holder.binding.about.text = description
            holder.binding.about.visibility = View.VISIBLE
        }

        val postedByUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(model.postedBy!!)
        postedByUserRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("ResourceAsColor")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Picasso.get()
                    .load(user?.profilePhoto)
                    .placeholder(R.drawable.kriana)
                    .into(holder.binding.profileImage)
                if (model.postImage.isNullOrEmpty()) {
                    holder.binding.shapeableImageView.visibility = View.GONE
                } else {
                    Picasso.get()
                        .load(model.postImage)
                        .placeholder(R.drawable.kriana)
                        .into(holder.binding.shapeableImageView)
                    holder.binding.shapeableImageView.visibility = View.VISIBLE
                }

                holder.binding.userName.text = user?.name
                holder.binding.lastName.text = user?.lastName
//                holder.binding.about.text = model.postDescription
                val description = model.postDescription
                if (!description.isNullOrEmpty()) {
                    val spannableString = SpannableString(description)
                    val pattern = Pattern.compile("#\\w+")
                    val matcher = pattern.matcher(description)
                    while (matcher.find()) {
                        val start = matcher.start()
                        val end = matcher.end()
                        spannableString.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    holder.binding.about.text = spannableString
                    holder.binding.about.visibility = View.VISIBLE
                } else {
                    holder.binding.about.visibility = View.GONE
                }


                val timestamp =
                    model.postedAt // Assuming model.postedAt is a Long value representing the timestamp
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - timestamp!!

                val timeAgo = DateUtils.getRelativeTimeSpanString(
                    timestamp,
                    currentTime,
                    DateUtils.MINUTE_IN_MILLIS
                )
                holder.binding.time.text = timeAgo.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        val likesRef = FirebaseDatabase.getInstance().reference.child("posts")
            .child(model.postId ?: "")
            .child("likes")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")

        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    holder.binding.like.setImageResource(R.drawable.red_fav)
                } else {
                    holder.binding.like.setOnClickListener {
                        FirebaseDatabase.getInstance().reference
                            .child("posts")
                            .child(model.postId!!)
                            .child("likes")
                            .child(FirebaseAuth.getInstance().uid!!)
                            .setValue(true)
                            .addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference
                                    .child("posts")
                                    .child(model.postId!!)
                                    .child("postLike")
                                    .setValue(model.postLike + 1)
                                    .addOnSuccessListener {
                                        holder.binding.like.setImageResource(R.drawable.red_fav)

                                        val notification = Notification()
                                        notification.notificationBy = FirebaseAuth.getInstance().uid
                                        notification.notificationAt = Date().time
                                        notification.postId = model.postId
                                        notification.postedBy = model.postedBy
                                        notification.type = "like"

                                        FirebaseDatabase.getInstance().reference
                                            .child("notification")
                                            .child(model.postedBy!!)
                                            .push()
                                            .setValue(notification)
                                    }
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

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
                            .into(holder.binding.profileComment)
                    } else {
                        holder.binding.profileImage.setImageResource(R.drawable.ic_message_icon)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        holder.binding.comment.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", model.postId)
            intent.putExtra("postedBy", model.postedBy)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}


