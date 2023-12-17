package com.example.quizletfinal

import TopicAdapter
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class FolderActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var receivedFolder: Folder
    private lateinit var folderName: TextView
    private lateinit var folderDescription: TextView
    private lateinit var deleteButton: TextView
    private lateinit var createTopicButton: Button
    private lateinit var myTopicList: RecyclerView
    private lateinit var ifNoTopic: LinearLayout
    private lateinit var adapter: TopicAdapter
    private lateinit var auth: FirebaseAuth
    private var editable: Boolean = false
    private var username: String = ""
    val topics = mutableListOf<Topic>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        auth = FirebaseAuth.getInstance()
        receivedFolder = intent.getParcelableExtra("folderData") ?: Folder()
        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "No Username") ?: ""
        editable = intent.getBooleanExtra("editable", false)

        folderName = findViewById(R.id.txtFolderName)
        folderDescription = findViewById(R.id.txtFolderDescription)
        deleteButton = findViewById(R.id.btnDeleteFolder)
        closeButton = findViewById(R.id.btnClose)
        createTopicButton = findViewById(R.id.btnOpenAddTopic)
        myTopicList = findViewById(R.id.myTopicList)
        ifNoTopic = findViewById(R.id.ifNoTopic)

        val topicNameText = receivedFolder.title
        val topicDescription = receivedFolder.description

        folderName.text = topicNameText
        folderDescription.text = topicDescription

        setupRecyclerView()
        receivedFolder.id?.let { loadTopics(it) }

        if (editable) {
            deleteButton.visibility = View.VISIBLE

            deleteButton.setOnClickListener {
                receivedFolder.id?.let { folderId ->
                    showConfirmationDialog(folderId)
                }
            }
            folderName.setOnClickListener {
                receivedFolder.id?.let { folderId ->
                    showEditDialog("title", folderId, folderName.text.toString())
                }
            }
            folderDescription.setOnClickListener {
                receivedFolder.id?.let { folderId ->
                    showEditDialog("description", folderId, folderDescription.text.toString())
                }
            }
        } else {
            deleteButton.visibility = View.GONE
        }

        createTopicButton.setOnClickListener {
            val intent = Intent(this@FolderActivity, AddTopicActivity::class.java).apply {
                putExtra("folderData", receivedFolder)
            }
            startActivity(intent)
        }

        closeButton.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        myTopicList.layoutManager = LinearLayoutManager(this)
        adapter = TopicAdapter(this, topics, onItemClickListener)
        myTopicList.adapter = adapter
    }

    private fun loadTopics(folderId: String) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            setCancelable(false)
            show()
        }

        FirebaseDatabase.getInstance().getReference("users")
            .child(username)
            .child("folders")
            .child(folderId)
            .child("topics")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val topicsMap = dataSnapshot.getValue(object : GenericTypeIndicator<Map<String, Topic>>() {})
                    val updatedTopics = topicsMap?.values?.toList() ?: emptyList()

                    adapter.topicList = updatedTopics.toMutableList()
                    adapter.notifyDataSetChanged()

                    ifNoTopic.visibility = if (updatedTopics.isEmpty()) View.VISIBLE else View.GONE

                    progressDialog.dismiss()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FolderActivity", "loadTopics:onCancelled", databaseError.toException())
                    progressDialog.dismiss()

                }
            })
    }

    private fun showConfirmationDialog(folderId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this folder?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            deleteFolder(folderId)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFolder(folderId: String) {
            val folderReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("folders").child(folderId)

            folderReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Folder deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete folder: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


    private fun showEditDialog(field: String, folderId: String, currentValue: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Update Folder ${field.capitalize(Locale.ROOT)}")
            val inputField = EditText(this@FolderActivity)
            inputField.setText(currentValue)
            setView(inputField)
            setPositiveButton("Update") { dialog, _ ->
                val newValue = inputField.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    updateFolder(field, folderId, newValue)
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        }
        builder.show()
    }

    private fun updateFolder(field: String, folderId: String, newValue: String) {
        val folderReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("folders").child(folderId)
        val updateMap = hashMapOf<String, Any>(field to newValue)
        folderReference.updateChildren(updateMap)
            .addOnSuccessListener {
                Toast.makeText(this, "$field updated successfully", Toast.LENGTH_SHORT).show()
                if (field.lowercase(Locale.ROOT) == "title") {
                    folderName.text = newValue
                } else if (field.lowercase(Locale.ROOT) == "description") {
                    folderDescription.text = newValue
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update $field: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private val onItemClickListener = object : OnItemClickListener {
        override fun onItemClickListener(topic: Topic) {
            val intent = Intent(this@FolderActivity, TopicActivity::class.java)
            intent.putExtra("topicData", topic)
            intent.putExtra("editable", true)
            startActivity(intent)
        }

        override fun onItemClickListener(folder: Folder) {
            TODO("Not yet implemented")
        }

        override fun onItemClickListener(card: Card) {
            TODO("Not yet implemented")
        }

        override fun onItemLongClickListener(card: Card) {
            TODO("Not yet implemented")
        }

        override fun onItemDeleteListener(topic: Topic) {
            TODO("Not yet implemented")
        }

        override fun onItemMoveListener(topic: Topic) {
            TODO("Not yet implemented")
        }
    }
}