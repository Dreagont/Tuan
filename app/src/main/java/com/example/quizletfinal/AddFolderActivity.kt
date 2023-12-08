package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.quizletfinal.models.Folder
import com.google.firebase.database.FirebaseDatabase

class AddFolderActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var folderNameEditText: EditText
    private lateinit var folderDescriptionEditText: EditText
    private lateinit var addButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_folder)

        closeButton = findViewById(R.id.btnClose)
        folderNameEditText = findViewById(R.id.folderName)
        folderDescriptionEditText = findViewById(R.id.folderDescription)
        addButton = findViewById(R.id.btnAddFolder)

        closeButton.setOnClickListener {
            finish()
        }

        val username = intent.getStringExtra("username")

        addButton.setOnClickListener {
            if (username != null) {
                addFolder(username)
            }
        }
    }
    private fun addFolder(username: String) {
        val folderName = folderNameEditText.text.toString()
        val folderDescription = folderDescriptionEditText .text.toString()

        if (folderName.isNotEmpty() && folderDescription.isNotEmpty()){
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("folders")
            val folderId = databaseReference.push().key
            val folder = Folder(folderId!!, folderName, folderDescription)

            databaseReference.child(folderId).setValue(folder)
                .addOnSuccessListener {
                    Toast.makeText(this, "Add folder successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add folder", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}