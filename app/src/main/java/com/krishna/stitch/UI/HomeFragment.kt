package com.krishna.stitch.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.krishna.stitch.Adapter.PostAdapter
import com.krishna.stitch.Adapter.StoryAdapter
import com.krishna.stitch.ChatActivity.ChatActivity
import com.krishna.stitch.R
import com.krishna.stitch.databinding.FragmentHomeBinding
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.Story
import com.krishna.stitch.model.User
import com.krishna.stitch.model.UserStories
import com.squareup.picasso.Picasso
import java.util.Date


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val postList: ArrayList<Post> = ArrayList()
    private var database: FirebaseDatabase? = null
    private var auth: FirebaseAuth? = null
    private var storage: FirebaseStorage? = null
    private var postAdapter: PostAdapter? = null
    var storyList = ArrayList<Story>()
    var galleryLauncher: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        postAdapter = PostAdapter(postList, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.dashboardRV.layoutManager = layoutManager
        binding.dashboardRV.adapter = postAdapter


        val storyAdapter = StoryAdapter(storyList, requireContext())
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.storyRV.layoutManager = linearLayoutManager
        binding.storyRV.isNestedScrollingEnabled = false
        binding.storyRV.adapter = storyAdapter

        database!!.reference
            .child("stories").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        storyList.clear()
                        for (storySnapshot in snapshot.children) {
                            val story = Story()
                            story.storyBy = storySnapshot.key.toString()
                            story.storyAt = storySnapshot.child("postedBy").getValue(Long::class.java)!!

                            val stories = ArrayList<UserStories?>()
                            for (snapshot1 in storySnapshot.child("userStories").children) {
                                val userStories = snapshot1.getValue(UserStories::class.java)
                                stories.add(userStories)
                            }
                            story.stories = stories
                            storyList.add(story)
                        }

                        storyAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
binding.message.setOnClickListener {
    var intent = Intent(requireContext(),ChatActivity::class.java)
    startActivity(intent)

}

        database!!.reference.child("Users").child(auth!!.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val user: User? = snapshot.getValue(User::class.java)

                        if (!user!!.profilePhoto.isNullOrEmpty()) {
                            Picasso.get()
                                .load(user.profilePhoto)
                                .placeholder(R.drawable.kriana)
                                .into(binding.addStoryImage)
                        } else {

                            binding.addStoryImage.setImageResource(R.drawable.ic_message_icon)
                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })



        database?.reference?.child("posts")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (dataSnapshot in snapshot.children) {
                    val post = dataSnapshot.getValue(Post::class.java)
                    post!!.postId = dataSnapshot.key
                    post?.let { postList.add(it) }
                }
                postAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        binding.addStoryImage.setOnClickListener(View.OnClickListener { galleryLauncher!!.launch("image/*") })
        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { result ->
            val imageView = binding.addStoryImage as ImageView
            imageView.setImageURI(result)
            val reference = storage!!.reference
                .child("stories")
                .child(FirebaseAuth.getInstance().uid!!)
                .child(Date().time.toString() + "")
            reference.putFile(result!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val story = Story()
                    story.storyAt = Date().time


                    database!!.reference
                        .child("stories")
                        .child(FirebaseAuth.getInstance().uid!!)
                        .child("postedBy")
                        .setValue(story.storyAt).addOnSuccessListener {
                            val stories = UserStories(uri.toString(), story.storyAt)
                            database!!.reference
                                .child("stories")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .child("userStories")
                                .push()
                                .setValue(stories).addOnSuccessListener {  }
                        }
                }
            }
        }


        return binding.root
    }
}
