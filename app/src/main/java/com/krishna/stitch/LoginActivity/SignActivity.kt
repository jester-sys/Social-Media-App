package com.krishna.stitch.LoginActivity

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.krishna.stitch.MainActivity
import com.krishna.stitch.databinding.ActivitySignBinding
import com.krishna.stitch.model.User


//class SignActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySignBinding
//    private lateinit var auth: FirebaseAuth
//    private  lateinit var database:FirebaseDatabase
//    var storage: FirebaseStorage? = null
//    private val availability = arrayOf("accountant", "chef", "economist", "coach", "businessperson", "bookkeeper", "banker")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//        storage = FirebaseStorage.getInstance()
//        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, availability)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.profession.adapter = adapter
//
//        binding.profilePhoto.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent, 10)
//
//        }
//        binding.loginBtn.setOnClickListener {
//            if (!binding.Name.text.toString().equals("")) {
//                val preferences = getSharedPreferences("UserLogin", MODE_PRIVATE)
//                val editor = preferences.edit()
//                editor.putBoolean("flag", true)
//                editor.apply()
//            }
//            val name = binding.Name.text.toString().trim()
//            val user: String = binding.emailET.getText().toString().trim()
//            val pass: String = binding.passwordET.getText().toString().trim()
//            if (name.isEmpty()) {
//                binding.Name.error = "Name cannot be empty"
//            }
//            if (user.isEmpty()) {
//                binding.emailET.setError("Email cannot be empty")
//            }
//            if (pass.isEmpty()) {
//                binding.passwordET.setError("Password cannot be empty")
//            } else {
//                auth.createUserWithEmailAndPassword(user, pass)
//                    .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
//                        override fun onComplete(task: Task<AuthResult?>) {
//                            if (task.isSuccessful) {
//                                var user = User(
//                                    binding.Name.text.toString(),
//                                    binding.SarName.text.toString(),
//                                    binding.emailET.text.toString(),
//                                    binding.passwordET.text.toString(),
//                                    binding.Gender.text.toString(),
//                                    binding.BirthDate.text.toString(),
//                                    binding.UserNameET.text.toString(),
//                                    binding.profession.toString(),
//                                    binding.profilePhoto.toString()
//
//                                )
//                                var id = task.result?.user?.uid
//                                if (id != null) {
//                                    database.reference.child("Users").child(id).setValue(user)
//                                }
//                                Toast.makeText(
//                                    this@SignActivity, "SignUp Successful", Toast.LENGTH_SHORT
//                                ).show()
//                                startActivity(Intent(this@SignActivity, MainActivity::class.java))
//                            } else {
//                                Toast.makeText(
//                                    this@SignActivity, "SignUp Failed", Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    })
//            }
//        };
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data?.data != null) {
//            val uri: Uri = data.data!!
//            val imageView = binding.profilePhoto as ImageView
//            imageView.setImageURI(uri)
//
//            val reference: StorageReference = storage?.reference
//                ?.child("profile_image")
//                ?.child(FirebaseAuth.getInstance().uid!!) ?: return
//
//            reference.putFile(uri).addOnSuccessListener { taskSnapshot ->
//                Toast.makeText(this, "Profile Photo Saved", Toast.LENGTH_SHORT).show()
//                reference.downloadUrl.addOnSuccessListener { downloadUri ->
//                    database?.reference?.child("Users")
//                        ?.child(auth?.uid!!)
//                        ?.child("profilePhoto")
//                        ?.setValue(downloadUri.toString())
//                }
//            }.addOnFailureListener { exception ->
//                Log.e("UploadError", "Failed to upload profile photo: ${exception.message}", exception)
//                Toast.makeText(this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
//

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var storage: FirebaseStorage? = null
    private val availability = arrayOf(
        "accountant",
        "chef",
        "economist",
        "coach",
        "businessperson",
        "bookkeeper",
        "banker"
    )

    private var profilePhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, availability)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.profession.adapter = adapter

        binding.profilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 10)
        }

        binding.loginBtn.setOnClickListener {
            val name = binding.Name.text.toString().trim()
            val lastName = binding.SarName.text.toString().trim()
            val email = binding.emailET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()
            val gender = binding.Gender.text.toString().trim()
            val birthDate = binding.BirthDate.text.toString().trim()
            val username = binding.UserNameET.text.toString().trim()
            val profession = binding.profession.selectedItem.toString()

            if (name.isEmpty()) {
                binding.Name.error = "Name cannot be empty"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailET.error = "Email cannot be empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordET.error = "Password cannot be empty"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            val user = User(
                                name = name,
                                lastName = lastName,
                                email = email,
                                password = password,
                                gender = gender,
                                birthDate = birthDate,
                                username = username,
                                profession = profession,
                                profilePhoto = "" // Initialize with empty string
                            )

                            database.reference.child("Users").child(userId).setValue(user)
                            uploadProfilePhoto(userId, profilePhotoUri)
                        }

                        Toast.makeText(this@SignActivity, "SignUp Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(this@SignActivity, "SignUp Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data?.data != null) {
            profilePhotoUri = data.data // Store the selected image URI
            binding.profilePhoto.setImageURI(profilePhotoUri) // Display the selected image in ImageView
        }
    }

    private fun uploadProfilePhoto(userId: String, imageUri: Uri?) {
        if (imageUri == null) return

        val imageRef = storage?.reference
            ?.child("profile_images")
            ?.child(userId)

        val uploadTask = imageRef?.putFile(imageUri)
        uploadTask?.addOnSuccessListener {
            Toast.makeText(this, "Profile Photo Uploaded Successfully", Toast.LENGTH_SHORT).show()

            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                database.reference.child("Users").child(userId)
                    .child("profilePhoto")
                    .setValue(downloadUri.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile Photo Saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save profile photo", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }?.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show()
        }
    }
}
