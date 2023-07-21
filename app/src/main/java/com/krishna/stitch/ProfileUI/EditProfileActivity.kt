package com.krishna.stitch.ProfileUI

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.krishna.stitch.R
import com.krishna.stitch.UI.ProfileFragment
import com.krishna.stitch.databinding.ActivityEditProfileBinding
import com.krishna.stitch.model.User
import com.squareup.picasso.Picasso




class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val availability = arrayOf("accountant", "chef", "economist", "coach", "businessperson", "bookkeeper", "banker")

    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availability)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

//    =====Fetch User Data From Database=====
        //        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//        changeCoverPhoto = view.findViewById(R.id.changeCoverPhoto);

//        recyclerView = view.findViewById(R.id.friendRecyclerView);


//    =====Fetch User Data From Database=====
        database!!.reference.child("Users").child(auth!!.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val user: User? = snapshot.getValue(User::class.java)

                        binding.NameET.setText(user?.name)
                        binding.LastNameET.setText(user?.lastName)
                        binding.UserNameET.setText(user?.username)

                        val professionIndex = availability.indexOf(user?.profession)
                        if (professionIndex != -1) {
                            binding.spinner.setSelection(professionIndex)
                        }

                        if (!user!!.profilePhoto.isNullOrEmpty()) {
                            Picasso.get()
                                .load(user.profilePhoto)
                                .placeholder(R.drawable.kriana)
                                .into(binding.EditProfileBtn)
                        } else {
                            // Set a default image if the profile photo is null or empty
                            binding.EditProfileBtn.setImageResource(R.drawable.ic_message_icon)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding.check.setOnClickListener {
            val bio = binding.Bio.text.toString().trim()
            val name = binding.NameET.text.toString().trim()
            val lastName = binding.LastNameET.text.toString().trim()
            val userName = binding.UserNameET.text.toString().trim()
            val profession = binding.spinner.selectedItem.toString().trim()
            val  profileImage = binding.EditProfileBtn
            val userId = auth?.currentUser?.uid

            if (userId != null) {
                val userRef = database?.reference?.child("Users")?.child(userId)
                userRef?.child("name")?.setValue(name)
                userRef?.child("bio")?.setValue(bio)
                userRef?.child("lastName")?.setValue(lastName)
                userRef?.child("username")?.setValue(userName)
                userRef?.child("profession")?.setValue(profession)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@EditProfileActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@EditProfileActivity, ProfileFragment::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }




        binding.EditProfileBtn.setOnClickListener {
            val sheetDialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
            val sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, null)

            sheetView.findViewById<TextView>(R.id.uploadImage).setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 22)
                sheetDialog.dismiss()
            }
            sheetDialog.setContentView(sheetView)
            sheetDialog.show()
        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 22 && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!
            val imageView = binding.EditProfileBtn as ImageView
            imageView.setImageURI(uri)

            val reference: StorageReference = storage?.reference
                ?.child("profile_image")
                ?.child(FirebaseAuth.getInstance().uid!!) ?: return

            reference.putFile(uri).addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Profile Photo Saved", Toast.LENGTH_SHORT).show()
                reference.downloadUrl.addOnSuccessListener { downloadUri ->
                    database?.reference?.child("Users")
                        ?.child(auth?.uid!!)
                        ?.child("profilePhoto")
                        ?.setValue(downloadUri.toString())
                }
            }.addOnFailureListener { exception ->
                Log.e("UploadError", "Failed to upload profile photo: ${exception.message}", exception)
                Toast.makeText(this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}










