package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.adapters.CardAdapter
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class TopicActivity : AppCompatActivity(), OnItemClickListener, TextToSpeech.OnInitListener { //here
    private lateinit var textToSpeech: TextToSpeech //here
    private lateinit var auth: FirebaseAuth
    private var receivedTopic: Topic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        receivedTopic = intent.getParcelableExtra("topicData")



        if (receivedTopic != null) {
            val deleteButton = findViewById<TextView>(R.id.btnDeleteTopic)
            val closeButton = findViewById<Button>(R.id.btnClose)
            val topicName = findViewById<TextView>(R.id.txtTopicName)
            val userName = findViewById<TextView>(R.id.txtUsername)
            val termNumber = findViewById<TextView>(R.id.txtTermNumber)
            val topicDescriptionView = findViewById<TextView>(R.id.txtTopicDescription)
            val flashCardGame = findViewById<LinearLayout>(R.id.btnFlashcard)
            val  multiChoice= findViewById<LinearLayout>(R.id.btnMultiple)

            auth = FirebaseAuth.getInstance()


            val cardListView = findViewById<RecyclerView>(R.id.cardListView)

            val username = receivedTopic!!.username
            val topicNameText = receivedTopic!!.title
            val topicDescription = receivedTopic!!.description
            val termNumberValue = receivedTopic!!.cards.size
            textToSpeech = TextToSpeech(this, this)

            topicName.text = topicNameText
            userName.text = username
            termNumber.text = termNumberValue.toString()
            topicDescriptionView.text = topicDescription

            closeButton.setOnClickListener { finish() }
            val cardList = receivedTopic!!.cards.values.toList()

            cardListView.layoutManager = LinearLayoutManager(applicationContext)

            var adapter = CardAdapter(applicationContext, cardList, this)
            cardListView.adapter = adapter

            flashCardGame.setOnClickListener {
                val intent = Intent(this, FlashcardActivity::class.java)
                intent.putExtra("topicData", receivedTopic)
                startActivity(intent)
            }

            multiChoice.setOnClickListener {
                val intent = Intent(this, TestSettingActivity::class.java)

                intent.putExtra("topicName", receivedTopic!!.title)
                intent.putExtra("cardList", ArrayList(cardList))
                intent.putExtra("game", "multi")

                startActivity(intent)
            }

            deleteButton.setOnClickListener {
                val topicId = receivedTopic!!.id
                if (topicId != null) {
                    showConfirmationDialog(topicId)
                }
            }


        } else {
            Toast.makeText(this, "No Topic data received", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showConfirmationDialog(topicId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this topic?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            deleteTopicFromFirebase(topicId)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteTopicFromFirebase(topicId: String) {
        val currentUser = auth.currentUser
        val username = receivedTopic?.username

        if (currentUser != null && username != null) {
            val userReference = FirebaseDatabase.getInstance().getReference("users")
            val topicReference = userReference.child(username).child("topics").child(topicId)

            // Remove the topic entry from Firebase
            topicReference.removeValue()
                .addOnSuccessListener {
                    showMessage("Topic deleted successfully")
                    finish()
                }
                .addOnFailureListener { e ->
                    showMessage("Failed to delete topic: ${e.message}")
                }
        } else {
            showMessage("User not authenticated or topic data not found")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClickListener(topic: Topic) {
        TODO("Not yet implemented")
    }

    override fun onItemClickListener(folder: Folder) {
        TODO("Not yet implemented")
    }

    override fun onItemClickListener(card: Card) {
        speakText(card.english)
    }

    private fun speakText(english: String) { //here
        textToSpeech.speak(english, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroy() {//here
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()

        super.onDestroy()
    }
    override fun onInit(status: Int) {//here
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language is not supported")
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed")
        }
    }
}