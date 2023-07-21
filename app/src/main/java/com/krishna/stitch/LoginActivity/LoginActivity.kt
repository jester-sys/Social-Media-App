package com.krishna.stitch.LoginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.krishna.stitch.MainActivity
import com.krishna.stitch.R
import com.krishna.stitch.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null // Change the type to nullable FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            auth.signInWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
        }

        binding.gotoSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser // Assign the current user here

        if (currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
