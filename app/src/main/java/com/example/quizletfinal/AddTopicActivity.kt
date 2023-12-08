package com.example.quizletfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Topic
import com.google.firebase.database.FirebaseDatabase

class AddTopicActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var topicTitleEditText: EditText
    private lateinit var topicDescriptionEditText: EditText
    private lateinit var visibilityRadioGroup: RadioGroup
    private lateinit var addCardTextView: TextView
    private lateinit var submitButton: Button

    private lateinit var cardContainer: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)

        closeButton = findViewById(R.id.btnClose)
        topicTitleEditText = findViewById(R.id.topicTitle)
        topicDescriptionEditText = findViewById(R.id.topicDescription)
        visibilityRadioGroup = findViewById(R.id.chooseVisible)
        addCardTextView = findViewById(R.id.btnAddCard)
        cardContainer = findViewById(R.id.container)
        submitButton = findViewById(R.id.btnAddTopic)

        val username = intent.getStringExtra("username")

        closeButton.setOnClickListener { finish() }

        addCardTextView.setOnClickListener { addCard() }

        submitButton.setOnClickListener {
            if (username != null)
            {
                addTopic(username)
            }
        }

    }

    private fun addCard() {
        val boxView = LayoutInflater.from(this).inflate(R.layout.topic_card, cardContainer, false)
        cardContainer.addView(boxView)
    }

    private fun addTopic(username : String) {
        val title = topicTitleEditText.text.toString()
        val description = topicDescriptionEditText.text.toString()
        val selectedRadioButtonId = visibilityRadioGroup.checkedRadioButtonId

        when {
            title.isEmpty() -> Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show()
            description.isEmpty() -> Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
            selectedRadioButtonId == -1 -> Toast.makeText(this, "Please choose an option.", Toast.LENGTH_SHORT).show()
            else -> {
                val visibility = if (visibilityRadioGroup.checkedRadioButtonId == R.id.radio_private) "private" else "public"

                val cardMap = mutableMapOf<String, Card>()

                for (i in 0 until cardContainer.childCount ) {
                    val boxView = cardContainer.getChildAt(i) as? LinearLayout
                    val wordEditText = boxView?.findViewById<EditText>(R.id.txtWordName)
                    val definitionEditText = boxView?.findViewById<EditText>(R.id.txtWordMean)

                    val term = wordEditText?.text.toString().trim()
                    val definition = definitionEditText?.text.toString().trim()

                    if (term.isNotEmpty() && definition.isNotEmpty()) {
                        cardMap[term] = Card(term, definition)
                    }
                }

                if (cardMap.size >= 2) {
                    val topicId = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics").push().key ?: return
                    val newTopic = Topic(topicId, title, description, visibility, null, cardMap)

                    saveTopic(newTopic, username)
                } else {
                    Toast.makeText(this, "Please fill out at least two topics", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveTopic(topic: Topic, username: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics")

        databaseReference.child(topic.id).setValue(topic)
            .addOnSuccessListener {
                finish()
                Toast.makeText(this, "Topic added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add topic", Toast.LENGTH_SHORT).show()
            }
    }
}