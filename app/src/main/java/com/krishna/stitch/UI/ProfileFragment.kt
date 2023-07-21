package com.krishna.stitch.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.krishna.stitch.Adapter.ViewPagerAdapter
import com.krishna.stitch.LoginActivity.LoginActivity
import com.krishna.stitch.ProfileUI.EditProfileActivity
import com.krishna.stitch.R
import com.krishna.stitch.Settings.NotificationSettingsActivity
import com.krishna.stitch.Settings.PrivacySettingsActivity
import com.krishna.stitch.Settings.ProfileSettingsActivity
import com.krishna.stitch.databinding.FragmentProfileBinding
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    var database: FirebaseDatabase? = null



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentProfileBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.pager.adapter = ViewPagerAdapter(requireActivity())
        val tabLayoutMediator = TabLayoutMediator(binding.tableLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Stitcher"
                }

                else -> {
                    tab.text = "Replay"
                }
            }
        }
        tabLayoutMediator.attach()

        binding.EditProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(),EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.Setting.setOnClickListener {
            showPopupMenu(it)
        }

        database!!.reference.child("Users").child(auth!!.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val user: User? = snapshot.getValue(User::class.java)

                        binding.Name.text = user!!.name
                        binding.UserName.text = user.username
                        binding.LastName.text = user.lastName
                        binding.Profession.text = user.profession
                        binding.Bio.text = user.bio

                        if (!user.profilePhoto.isNullOrEmpty()) {
                            Picasso.get()
                                .load(user.profilePhoto)
                                .placeholder(R.drawable.kriana)
                                .into(binding.profileImage)
                        } else {
                            // Set a default image if the profile photo is null or empty
                            binding.profileImage.setImageResource(R.drawable.ic_message_icon)
                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        val userId = auth?.currentUser?.uid
        val userRef = database?.reference?.child("Users")?.child(userId!!)

        userRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    // Update the UI with the followerCount
                    binding.followers.text = "${user.followerCount} Follower"
                    binding.following.text = "${user.followingCount} Following"
                    binding.Posts.text ="${user.postCount} Posts"

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


//        binding.Setting.setOnClickListener {
//            auth!!.signOut()
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
//
//
//        }

        return binding.root
    }
    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_profile -> {
                    // Handle "Edit Profile" menu item click
                    openProfileSettings()
                    true
                }
                R.id.menu_item_notifications -> {
                    // Handle "Notifications" menu item click
                    openNotificationSettings()
                    true
                }
                R.id.menu_item_privacy -> {
                    // Handle "Privacy" menu item click
                    openPrivacySettings()
                    true
                }
                R.id.menu_item_logout -> {
                    // Handle "Logout" menu item click
                    logoutUser()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun openProfileSettings() {


        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun openNotificationSettings() {
        // Implement your logic to open the notification settings screen/activity
        // For example, you can start a new activity:
        val intent = Intent(requireContext(), NotificationSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun openPrivacySettings() {
        // Implement your logic to open the privacy settings screen/activity
        // For example, you can start a new activity:
        val intent = Intent(requireContext(), PrivacySettingsActivity::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        // Implement your logic to log out the user
        // For example, you can sign out the user from Firebase Authentication:
        FirebaseAuth.getInstance().signOut()

        // Start the LoginActivity or navigate to the login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }


}


