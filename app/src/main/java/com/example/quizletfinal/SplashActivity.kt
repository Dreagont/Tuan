package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    private lateinit var btnSignUp: Button
    private lateinit var btnLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        btnSignUp = findViewById(R.id.btnRegister)
        btnLogIn = findViewById(R.id.btnLogin)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
            finish()
        }
        btnLogIn.setOnClickListener {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }
}