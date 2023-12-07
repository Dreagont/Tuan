package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var BackButton:Button
    private lateinit var EmailEditText: EditText
    private lateinit var SubmitButton: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        BackButton = findViewById(R.id.btnBackSplash)
        EmailEditText = findViewById(R.id.txtEmail)
        SubmitButton = findViewById(R.id.btnForgotPassword)

        auth = FirebaseAuth.getInstance()

        BackButton.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
            finish()
        }

        SubmitButton.setOnClickListener {
            val email = EmailEditText.text.toString()
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    startActivity(Intent(this@ForgotPasswordActivity, ForgotPasswordActivity::class.java))
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