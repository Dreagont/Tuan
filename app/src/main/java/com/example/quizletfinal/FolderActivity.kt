package com.example.quizletfinal

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizletfinal.models.Folder
import com.google.firebase.auth.FirebaseAuth

class FolderActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var receivedFolder: Folder
    private lateinit var folderName: TextView
    private lateinit var folderDescription: TextView
    private lateinit var deleteButton: TextView
    private lateinit var auth: FirebaseAuth
    private var editable: Boolean = false
    private var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        closeButton = findViewById(R.id.btnClose)

        receivedFolder = intent.getParcelableExtra("folderData") ?: Folder()
        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "No Username") ?: ""
        editable = intent.getBooleanExtra("editable", false)

        if (receivedFolder != null) {
            folderName = findViewById(R.id.txtFolderName)
            folderDescription = findViewById(R.id.txtFolderDescription)

            val topicNameText = receivedFolder!!.title
            val topicDescription = receivedFolder!!.description

            folderName.text = topicNameText
            folderDescription.text = topicDescription

            if (editable) {
                deleteButton.visibility = View.VISIBLE

                deleteButton.setOnClickListener {
                    receivedFolder.id?.let { folderId ->
//                        showConfirmationDialog(folderId)
                    }
                }
                folderName.setOnClickListener {
                    receivedFolder.id?.let { folderId ->
//                        showEditDialog("title", folderId, folderName.text.toString())
                    }
                }
                folderDescription.setOnClickListener {
                    receivedFolder.id?.let { folderId ->
//                        showEditDialog("description", folderId, folderDescription.text.toString())
                    }
                }
            }
        }


        closeButton.setOnClickListener { finish() }
    }


}