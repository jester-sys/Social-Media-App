package com.krishna.stitch.UI

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import java.util.regex.Pattern

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.krishna.stitch.R
import com.krishna.stitch.databinding.FragmentCreatePostBinding
import com.krishna.stitch.model.Post
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso
import java.util.Date

//class CreatePostFragment : Fragment() {
//    private lateinit var binding: FragmentCreatePostBinding
//    private var uri: Uri? = null
//    private var auth: FirebaseAuth? = null
//    private var database: FirebaseDatabase? = null
//    private var storage: FirebaseStorage? = null
//    private var dialog: ProgressDialog? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//        storage = FirebaseStorage.getInstance()
//        dialog = ProgressDialog(requireContext())
//
//        dialog!!.apply {
//            setProgressStyle(ProgressDialog.STYLE_SPINNER)
//            setTitle("Post Uploading")
//            setMessage("Please Wait...")
//            setCancelable(false)
//            setCanceledOnTouchOutside(false)
//        }
//
//        database!!.getReference().child("Users").child(auth!!.uid!!).addValueEventListener(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue(User::class.java)
//                    Picasso.get()
//                        .load(user?.profilePhoto)
//                        .placeholder(R.drawable.kriana)
//                        .into(binding.profileImage)
//                    binding.name.text = user?.name
//                    binding.lastname.text = user?.lastName
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle onCancelled event
//            }
//        })
//
//        binding.postDescription.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                val description = binding.postDescription.text.toString()
//                if (!description.isEmpty()) {
//                    binding.postBtn.apply {
//                        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.text_desgin))
//                        setTextColor(ContextCompat.getColor(context, R.color.white))
//                    }
//                    binding.addImage.visibility = View.VISIBLE
//                    binding.postBtn.isEnabled = true
//                } else {
//                    binding.postBtn.apply {
//                        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn))
//                        setTextColor(ContextCompat.getColor(context, R.color.silver))
//                    }
//                    binding.addImage.visibility = View.GONE
//                    binding.postBtn.isEnabled = false
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        })
//
//        binding.addImage.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent, 10)
//        }
//
//        binding.postBtn.setOnClickListener {
//            dialog!!.show()
//
//            val reference = storage!!.reference.child("posts")
//                .child(auth!!.uid!!)
//                .child(Date().time.toString())
//
//            reference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
//                reference.downloadUrl.addOnSuccessListener { uri ->
//                    val post = Post().apply {
//                        postImage = uri.toString()
//                        postedBy = auth!!.uid
//                        postDescription = binding.postDescription.text.toString()
//                        postedAt = Date().time
//                    }
//                    val postRef = database!!.reference.child("posts").push()
//                    postRef.setValue(post).addOnSuccessListener {
//                        dialog!!.dismiss()
//                        Toast.makeText(requireContext(), "Posted Successfully", Toast.LENGTH_SHORT).show()
//
//                        // Increase post count for the current user
//                        val currentUserRef = database!!.reference
//                            .child("Users")
//                            .child(auth!!.uid!!)
//                        currentUserRef.child("postCount").addListenerForSingleValueEvent(object :
//                            ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val currentPostCount = snapshot.getValue(Int::class.java) ?: 0
//                                currentUserRef.child("postCount").setValue(currentPostCount + 1)
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                // Handle the error
//                            }
//                        })
//                    }
//                }
//            }
//        }
//
//        return binding.root
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (data?.data != null) {
//            uri = data.data
//            binding.postImage.setImageURI(uri)
//            binding.postImage.visibility = View.VISIBLE
//            binding.postBtn.apply {
//                setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.text_desgin))
//                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                isEnabled = true
//            }
//        }
//
//    }
//}
//class CreatePostFragment : Fragment() {
//    private lateinit var binding: FragmentCreatePostBinding
//    private var uri: Uri? = null
//    private var auth: FirebaseAuth? = null
//    private var database: FirebaseDatabase? = null
//    private var storage: FirebaseStorage? = null
//    private var dialog: ProgressDialog? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//        storage = FirebaseStorage.getInstance()
//        dialog = ProgressDialog(requireContext())
//
//        dialog!!.apply {
//            setProgressStyle(ProgressDialog.STYLE_SPINNER)
//            setTitle("Post Uploading")
//            setMessage("Please Wait...")
//            setCancelable(false)
//            setCanceledOnTouchOutside(false)
//        }
//
//        database!!.getReference().child("Users").child(auth!!.uid!!)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val user = snapshot.getValue(User::class.java)
//                        Picasso.get()
//                            .load(user?.profilePhoto)
//                            .placeholder(R.drawable.kriana)
//                            .into(binding.profileImage)
//                        binding.name.text = user?.name
//                        binding.lastname.text = user?.lastName
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle onCancelled event
//                }
//            })
//
//        binding.postDescription.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                val description = binding.postDescription.text.toString()
//                if (!description.isEmpty()) {
//                    binding.postBtn.apply {
//                        setBackgroundDrawable(
//                            ContextCompat.getDrawable(
//                                context,
//                                R.drawable.text_desgin
//                            )
//                        )
//                        setTextColor(ContextCompat.getColor(context, R.color.white))
//                    }
//                    binding.addImage.visibility = View.VISIBLE
//                    binding.postBtn.isEnabled = true
//                } else {
//                    binding.postBtn.apply {
//                        setBackgroundDrawable(
//                            ContextCompat.getDrawable(
//                                context,
//                                R.drawable.follow_active_btn
//                            )
//                        )
//                        setTextColor(ContextCompat.getColor(context, R.color.silver))
//                    }
//                    binding.addImage.visibility = View.GONE
//                    binding.postBtn.isEnabled = false
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        })
//
//        binding.addImage.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent, 10)
//        }
//
//        binding.postBtn.setOnClickListener {
//            dialog!!.show()
//
//            val reference = storage!!.reference.child("posts")
//                .child(auth!!.uid!!)
//                .child(Date().time.toString())
//
//            uri?.let {
//                reference.putFile(it).addOnSuccessListener { taskSnapshot ->
//                    reference.downloadUrl.addOnSuccessListener { uri ->
//                        var postDescription = binding.postDescription.text.toString()
//                        val hashtags = extractHashtags(postDescription) // Extract hashtags from the post description
//
//                        val post = Post().apply {
//                            postImage = uri.toString()
//                            postedBy = auth!!.uid
//                            postDescription = postDescription
//                            postedAt = Date().time
//                            this.hashtags = hashtags // Assign the extracted hashtags to the post
//                        }
//
//                        val postRef = database!!.reference.child("posts").push()
//                        postRef.setValue(post).addOnSuccessListener {
//                            dialog!!.dismiss()
//                            Toast.makeText(
//                                requireContext(),
//                                "Posted Successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            // Increase post count for the current user
//                            val currentUserRef = database!!.reference
//                                .child("Users")
//                                .child(auth!!.uid!!)
//                            currentUserRef.child("postCount")
//                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                    override fun onDataChange(snapshot: DataSnapshot) {
//                                        val currentPostCount =
//                                            snapshot.getValue(Int::class.java) ?: 0
//                                        currentUserRef.child("postCount")
//                                            .setValue(currentPostCount + 1)
//                                    }
//
//                                    override fun onCancelled(error: DatabaseError) {
//                                        // Handle the error
//                                    }
//                                })
//                        }
//                        }
//                    }
//                }
//            }
//
//
//        return binding.root
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (data?.data != null) {
//            uri = data.data
//            binding.postImage.setImageURI(uri)
//            binding.postImage.visibility = View.VISIBLE
//            binding.postBtn.apply {
//                setBackgroundDrawable(
//                    ContextCompat.getDrawable(
//                        requireContext(),
//                        R.drawable.text_desgin
//                    )
//                )
//                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                isEnabled = true
//            }
//        }
//    }
//
//    private fun extractHashtags(postDescription: String): List<String> {
//        val hashtags = mutableListOf<String>()
//        val pattern = Pattern.compile("#\\w+")
//        val matcher = pattern.matcher(postDescription)
//        while (matcher.find()) {
//            val hashtag = matcher.group().substring(1) // Exclude the "#" symbol
//            hashtags.add(hashtag)
//        }
//        return hashtags
//    }
//
//}

class CreatePostFragment : Fragment() {
    private lateinit var binding: FragmentCreatePostBinding
    private var uri: Uri? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var dialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(requireContext())

        dialog!!.apply {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setTitle("Post Uploading")
            setMessage("Please Wait...")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        database!!.getReference().child("Users").child(auth!!.uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get()
                        .load(user?.profilePhoto)
                        .placeholder(R.drawable.kriana)
                        .into(binding.profileImage)
                    binding.name.text = user?.name
                    binding.lastname.text = user?.lastName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })

        binding.postDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val description = binding.postDescription.text.toString()
                if (!description.isEmpty()) {
                    binding.postBtn.apply {
                        setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.text_desgin
                            )
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                    binding.addImage.visibility = View.VISIBLE
                    binding.postBtn.isEnabled = true
                } else {
                    binding.postBtn.apply {
                        setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.follow_active_btn
                            )
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.silver))
                    }
                    binding.addImage.visibility = View.GONE
                    binding.postBtn.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.addImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 10)
        }

        binding.postBtn.setOnClickListener {
            dialog!!.show()

            if (uri != null) {
                val reference = storage!!.reference.child("posts")
                    .child(auth!!.uid!!)
                    .child(Date().time.toString())

                reference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        val post = Post().apply {
                            postImage = uri.toString()
                            postedBy = auth!!.uid
                            postDescription = binding.postDescription.text.toString()
                            postedAt = Date().time
                        }
                        savePost(post)
                    }
                }
            } else {
                val post = Post().apply {
                    postedBy = auth!!.uid
                    postDescription = binding.postDescription.text.toString()
                    postedAt = Date().time
                }
                savePost(post)
            }
        }




        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            uri = data.data
            binding.postImage.setImageURI(uri)
            binding.postImage.visibility = View.VISIBLE
            binding.postBtn.apply {
                setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.text_desgin))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                isEnabled = true
            }
        }

    }
    private fun savePost(post: Post) {
        val postRef = database!!.reference.child("posts").push()
        postRef.setValue(post).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(requireContext(), "Posted Successfully", Toast.LENGTH_SHORT).show()

            // Increase post count for the current user
            val currentUserRef = database!!.reference
                .child("Users")
                .child(auth!!.uid!!)
            currentUserRef.child("postCount").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentPostCount = snapshot.getValue(Int::class.java) ?: 0
                    currentUserRef.child("postCount").setValue(currentPostCount + 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }
    }

}