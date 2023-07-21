package com.krishna.stitch.UtilClass


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.krishna.stitch.databinding.FragmentBootomSheetDilogBinding


class bottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBootomSheetDilogBinding
    private var auth: FirebaseAuth? = null
    private var storage: FirebaseStorage? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBootomSheetDilogBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        val uploadImageTextView = binding.uploadImage
        val deleteImageTextView = binding.deleteImage

        uploadImageTextView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 22)
            dismiss()
        }

        deleteImageTextView.setOnClickListener {
            // Handle click for deleteImageTextView
            dismiss() // Dismiss the bottom sheet dialog
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 22 && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!
            val imageView = binding.uploadImage as ImageView
            imageView.setImageURI(uri)

            val reference: StorageReference = storage?.reference
                ?.child("profile_image")
                ?.child(FirebaseAuth.getInstance().uid!!) ?: return

            reference.putFile(uri).addOnSuccessListener { taskSnapshot ->
                Toast.makeText(context, "Profile Photo Saved", Toast.LENGTH_SHORT).show()
                reference.downloadUrl.addOnSuccessListener { downloadUri ->
                    database?.reference?.child("Users")
                        ?.child(auth?.uid!!)
                        ?.child("profilePhoto")
                        ?.setValue(downloadUri.toString())
                }
            }.addOnFailureListener { exception ->
                Log.e("UploadError", "Failed to upload profile photo: ${exception.message}", exception)
                Toast.makeText(context, "Failed to upload profile photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
