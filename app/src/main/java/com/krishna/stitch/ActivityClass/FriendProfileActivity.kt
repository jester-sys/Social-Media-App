package com.krishna.stitch.ActivityClass

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krishna.stitch.Adapter.PostAdapter
import com.krishna.stitch.Adapter.UserAdapter
import com.krishna.stitch.Adapter.ViewPagerAdapter
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ActivityFriendProfileBinding

import com.krishna.stitch.model.Follow
import com.krishna.stitch.model.Notification
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.Date

class FriendProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendProfileBinding
    private val postList = ArrayList<User>()
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var adapter: UserAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val userName = intent.getStringExtra("Name")
        val lastName = intent.getStringExtra("lastName")
        val profession = intent.getStringExtra("Profession")
        val bio = intent.getStringExtra("Bio")
        val username = intent.getStringExtra("UserName")
        val imageURL = intent.getStringExtra("ImageURL")

        Picasso.get().load(imageURL).into(binding.profileImage)

        binding.UserName.text = username
        binding.Bio.text = bio
        binding.Profession.text = profession
        binding.LastName.text = lastName
        binding.Name.text = userName

        val followers = intent.getStringExtra("followers")
        val following = intent.getStringExtra("Following")
        val Posts = intent.getStringExtra("Posts")
        binding.following.text = following
        binding.Posts.text = Posts
        binding.followers.text = followers


    }
}





