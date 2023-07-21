package com.krishna.stitch.Adapter

import android.accessibilityservice.GestureDescription
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.R
import com.krishna.stitch.databinding.StoryRvDesignBinding
import com.krishna.stitch.model.Story
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory


class StoryAdapter(var list: ArrayList<Story>, var context: Context) :
    RecyclerView.Adapter<StoryAdapter.viewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.story_rv_design, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val story = list[position]
        val stories1 = story.stories
        if (stories1!!.size > 0) {
            val image = stories1[stories1.size - 1]!!.image
            Picasso.get()
                .load(image)
                .into(holder.binding.profileImage)
            holder.binding.statusCircle.setPortionsCount(stories1.size)
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(story.storyBy).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user: User? = snapshot.getValue(User::class.java)
                        Picasso.get()
                            .load(user?.profilePhoto)
                            .placeholder(R.drawable.kriana)
                            .into(holder.binding.profileImage)
                        holder.binding.name.text = user?.name
                        holder.binding.profileImage.setOnClickListener {
                            val myStories: ArrayList<MyStory> = ArrayList<MyStory>()
                            for (storyItem in stories1) {
                                myStories.add(
                                    MyStory(
                                        storyItem!!.image
                                    )
                                )
                            }
                            StoryView.Builder((context as AppCompatActivity).supportFragmentManager)
                                .setStoriesList(myStories) // Required
                                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                .setTitleText(user?.name) // Default is Hidden
//                                .setSubtitleText(user.getProfession()) // Default is Hidden
                                .setTitleLogoUrl(user?.profilePhoto) // Default is Hidden
                                .setStoryClickListeners(object : StoryClickListeners {
                                    override fun onDescriptionClickListener(position: Int) {
                                        //your action
                                    }

                                    override fun onTitleIconClickListener(position: Int) {
                                        //your action
                                    }
                                }) // Optional Listeners
                                .build() // Must be called before calling show method
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: StoryRvDesignBinding

        init {
            binding = StoryRvDesignBinding.bind(itemView)
        }
    }
}
