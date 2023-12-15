package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizletfinal.models.Topic

class TopicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val receivedTopic = intent.getParcelableExtra<Topic>("topicData")

        if (receivedTopic != null) {
            val closeButton = findViewById<Button>(R.id.btnClose)
            val topicName = findViewById<TextView>(R.id.txtTopicName)
            val userName = findViewById<TextView>(R.id.txtUsername)
            val termNumber = findViewById<TextView>(R.id.txtTermNumber)
            val topicDescriptionView = findViewById<TextView>(R.id.txtTopicDescription)
            val flashCardGame = findViewById<LinearLayout>(R.id.btnFlashcard)

            val username = receivedTopic.username
            val topicNameText = receivedTopic.title
            val topicDescription = receivedTopic.description
            val termNumberValue = receivedTopic.cards.size

            topicName.text = topicNameText
            userName.text = username
            termNumber.text = termNumberValue.toString()
            topicDescriptionView.text = topicDescription

            closeButton.setOnClickListener { finish() }

            flashCardGame.setOnClickListener {
                val intent = Intent(this, FlashcardActivity::class.java)
                intent.putExtra("topicData", receivedTopic)
                startActivity(intent)
            }

        } else {
            Toast.makeText(this, "No Topic data received", Toast.LENGTH_SHORT).show()
        }
    }
}

