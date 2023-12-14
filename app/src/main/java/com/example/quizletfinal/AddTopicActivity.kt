package com.example.quizletfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        val sharedPreferences = this.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("Username", "No Username")

        val cardList: ArrayList<Card>? = intent.getParcelableArrayListExtra("csvReadCardList")

        if (cardList != null) {
            findViewById<ScrollView>(R.id.cardListScroll).visibility = View.GONE
            submitButton.setOnClickListener {

                if (username != null)
                {
                    addTopicCsv(username, cardList)
                }
            }
        } else {
            findViewById<ScrollView>(R.id.cardListScroll).visibility = View.VISIBLE
            submitButton.setOnClickListener {

                if (username != null)
                {
                    addTopic(username)
                }
            }
        }

        findViewById<LinearLayout>(R.id.addWithCsv).setOnClickListener {
            val intent = Intent(this, CsvReaderActivity::class.java)
            intent.putExtra("username",username)
            startActivity(intent)
        }


        closeButton.setOnClickListener { finish() }

        addCardTextView.setOnClickListener { addCard() }



    }

    private fun addTopicCsv(username: String, cardList: ArrayList<Card>) {
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

                // Add cards from the CSV file
                for (card in cardList) {
                    cardMap[card.english] = card
                }

                if (cardMap.size >= 2) {
                    val topicId = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics").push().key
                        ?: return
                    val newTopic = Topic(topicId, username, title, description, visibility, null, cardMap)

                    saveTopic(newTopic, username)
                } else {
                    Toast.makeText(this, "Please fill out at least two topics", Toast.LENGTH_SHORT).show()
                }
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
                    val newTopic = Topic(topicId, username, title, description, visibility, null, cardMap)

                    saveTopic(newTopic, username)
                } else {
                    Toast.makeText(this, "Please fill out at least two topics", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveTopic(topic: Topic, username: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics")

        databaseReference.child(topic.id!!).setValue(topic)
            .addOnSuccessListener {
                finish()
                Toast.makeText(this, "Topic added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add topic", Toast.LENGTH_SHORT).show()
            }
    }
}