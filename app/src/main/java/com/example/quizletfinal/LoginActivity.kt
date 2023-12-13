package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.quizletfinal.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var backButton: Button
    private lateinit var forgetPasswordButton: LinearLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        userNameEditText = findViewById(R.id.txtUsername)
        passwordEditText = findViewById(R.id.txtPassword)
        loginButton = findViewById(R.id.btnLogin)
        backButton = findViewById(R.id.btnBackSplash)
        forgetPasswordButton = findViewById(R.id.btnForgotPassword)
        auth = FirebaseAuth.getInstance()
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { loginUser() }
        backButton.setOnClickListener { finish() }
        forgetPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = userNameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter email and password")
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful && auth.currentUser?.isEmailVerified == true) {
                auth.currentUser?.let { saveUserData(it) }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast(task.exception?.message ?: "Authentication failed.")
            }
        }
    }

    private fun saveUserData(firebaseUser: FirebaseUser) {
        readWithEmail(firebaseUser.email) { dataSnapshot ->
            dataSnapshot.children.forEach { userSnapshot ->
                if (userSnapshot.child("email").value.toString() == firebaseUser.email) {
                    with(getSharedPreferences("UserDetails", MODE_PRIVATE).edit()) {
                        putString("Username", userSnapshot.child("username").value.toString())
                        putString("Email", firebaseUser.email)
                        apply()
                    }
                    return@readWithEmail
                }
            }
        }
    }

    private fun readWithEmail(email: String?, processSnapshot: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    processSnapshot(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
