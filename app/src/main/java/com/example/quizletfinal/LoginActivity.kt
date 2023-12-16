package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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
        backButton.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast(task.exception?.message ?: "Authentication failed.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}