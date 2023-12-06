package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var userName: EditText
    private lateinit var passWord: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnBack: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userName = findViewById(R.id.txtUsername)
        passWord = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBackSplash)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

//        if (auth.currentUser != null) {
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
//        }

        btnLogin.setOnClickListener { loginUser() }
        btnBack.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SplashActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = userName.text.toString().trim() // Assuming this EditText is for email
        val password = passWord.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val verification = auth.currentUser?.isEmailVerified
                    if (verification == true) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.let { saveUserData(it) }
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Please verify your email!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(firebaseUser: FirebaseUser) {
        val sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Email", firebaseUser.email)
        editor.apply()
    }



}