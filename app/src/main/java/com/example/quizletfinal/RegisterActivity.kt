package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.quizletfinal.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        backButton = findViewById(R.id.btnBackSplash)
        userNameEditText = findViewById(R.id.txtUsername)
        emailEditText = findViewById(R.id.txtEmail)
        phoneEditText = findViewById(R.id.txtPhone)
        passwordEditText = findViewById(R.id.txtPassword)
        confirmPasswordEditText = findViewById(R.id.txtConfirmPassword)
        signUpButton = findViewById(R.id.btnSignUp)

        auth = FirebaseAuth.getInstance()

        backButton.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, SplashActivity::class.java))
            finish()
        }

        signUpButton.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val username = userNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (!validateInput(email, password, confirmPassword)) return

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                addUserToDatabase(username, email, phone)
                                showMessage("Registered successfully. Please check your email for verification.")
                            } else {
                                showMessage("Failed to send verification email: ${verificationTask.exception?.message}")
                            }
                        }
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    showMessage("Authentication failed: $errorMessage")
                    Log.e("RegisterActivity", "Authentication failed: $errorMessage")
                }
            }
    }

    private fun addUserToDatabase(username: String, email: String, phone: String) {
        val user = User(username, email, phone, "Default.jpg")
        FirebaseDatabase.getInstance().reference.child("users").child(username).setValue(user)
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                showMessage("All fields are required")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailEditText.error = "Invalid email format"
                false
            }
            password != confirmPassword -> {
                showMessage("Passwords do not match")
                false
            }
            password.length < 6 -> {
                showMessage("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}