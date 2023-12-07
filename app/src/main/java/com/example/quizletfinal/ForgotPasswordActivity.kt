package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var backButton:Button
    private lateinit var emailEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        backButton = findViewById(R.id.btnBackSplash)
        emailEditText = findViewById(R.id.txtEmail)
        submitButton = findViewById(R.id.btnForgotPassword)

        auth = FirebaseAuth.getInstance()

        backButton.setOnClickListener {
            finish()
        }

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    startActivity(Intent(this, ForgotPasswordActivity::class.java))
                    finish()
                    showMessage("Please check your email!")
                }
                .addOnFailureListener {
                    showMessage(it.toString())
                }
        }
    }
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}