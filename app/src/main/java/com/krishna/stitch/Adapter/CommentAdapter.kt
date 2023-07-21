package com.krishna.stitch.Adapter

import android.content.Context
import android.text.Html
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.R
import com.krishna.stitch.databinding.CommentSampleBinding
import com.krishna.stitch.model.Comment
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso

class CommentAdapter(var context: Context, list: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.viewHolder>() {
    var list: ArrayList<Comment>

    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.comment_sample, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val comment: Comment = list[position]
//        val time: String = TimeAgo.using(comment.commentedAt)
//        holder.binding.time.setText(time)

        val timestamp = comment.commentedAt // Assuming model.postedAt is a Long value representing the timestamp

        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - timestamp!!

        val timeAgo = DateUtils.getRelativeTimeSpanString(timestamp, currentTime, DateUtils.MINUTE_IN_MILLIS)

        holder.binding.time.text = timeAgo.toString()

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(comment.commentedBy)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)
                    Picasso.get()
                        .load(user?.profilePhoto)
                        .placeholder(R.drawable.kriana)
                        .into(holder.binding.profileImage)
                    holder.binding.comment.text = comment.commentBody
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: CommentSampleBinding

        init {
            binding = CommentSampleBinding.bind(itemView)
        }
    }
}
