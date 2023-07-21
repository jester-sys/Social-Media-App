package com.krishna.stitch.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.krishna.stitch.ActivityClass.CommentActivity
import com.krishna.stitch.R
import com.krishna.stitch.databinding.AllNorificationBinding
import com.krishna.stitch.model.Notification
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso


internal class NotificationAdapter(private var context: Context, private var list: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AllNorificationBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.all_norification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        val type = notification.type

        FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(notification.notificationBy!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {

                        val profilePhoto = user.profilePhoto
                        if (profilePhoto.isNotEmpty()) {
                            Picasso.get()
                                .load(profilePhoto)
                                .placeholder(R.drawable.kriana)
                                .into(holder.binding.profileImage)
                        } else {

                            holder.binding.profileImage.setImageResource(R.drawable.ic_message_icon)
                        }

                        val timestamp = notification.notificationAt // Assuming model.postedAt is a Long value representing the timestamp  Path must not be empty.


                        val currentTime = System.currentTimeMillis()
                        val elapsedTime = currentTime - timestamp!!
                        val timeAgo = DateUtils.getRelativeTimeSpanString(timestamp, currentTime, DateUtils.MINUTE_IN_MILLIS)
                        holder.binding.time.text = timeAgo.toString()
                        val notificationText = when (type) {
                            "like" -> "<b>${user.name}${user.lastName}</b> Liked your post"
                            "comment" -> "<b>${user.name}${user.lastName}</b> Commented on your post"
                            else -> "<b>${user.name}${user.lastName}</b> Starts following you"
                        }
                        holder.binding.notification.text = Html.fromHtml(notificationText)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        holder.binding.openNotification.setOnClickListener {
            if (type != "follow") {
                FirebaseDatabase.getInstance().getReference()
                    .child("notification")
                    .child(notification.postedBy!!)
                    .child(notification.notificationId!!)
                    .child("checkOpen")
                    .setValue(true)

                holder.binding.openNotification.setBackgroundColor(Color.WHITE)
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("postId", notification.postId)
                intent.putExtra("postedBy", notification.postedBy)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }

        val checkOpen = notification.checkOpen
        if (checkOpen == true) {
            holder.binding.openNotification.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
