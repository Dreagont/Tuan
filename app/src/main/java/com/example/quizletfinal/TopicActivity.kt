package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.example.quizletfinal.adapters.CardAdapter
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class TopicActivity : AppCompatActivity(), OnItemClickListener, TextToSpeech.OnInitListener { //here
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var auth: FirebaseAuth
    private lateinit var deleteButton: TextView
    private lateinit var closeButton: Button
    private lateinit var topicName: TextView
    private lateinit var userName: TextView
    private lateinit var termNumber: TextView
    private lateinit var topicDescriptionView: TextView
    private lateinit var flashCardGame: LinearLayout
    private lateinit var multiChoice: LinearLayout
    private lateinit var btnTextGame: LinearLayout
    private lateinit var cardListView: RecyclerView
    private lateinit var leaderBoard: TextView
    private lateinit var username: String


    private var receivedTopic: Topic? = null
    private var editable: Boolean = false
    var loginUser: String? = ""
    private var cardList = ArrayList<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        receivedTopic = intent.getParcelableExtra("topicData")
        editable = intent.getBooleanExtra("editable", false)
        loginUser = intent.getStringExtra("loginUser")

        if (receivedTopic != null) {
            cardList = ArrayList(receivedTopic!!.cards.values)
            deleteButton = findViewById(R.id.btnDeleteTopic)
            closeButton = findViewById(R.id.btnClose)
            topicName = findViewById(R.id.txtTopicName)
            userName = findViewById(R.id.txtUsername)
            termNumber = findViewById(R.id.txtTermNumber)
            topicDescriptionView = findViewById(R.id.txtTopicDescription)
            flashCardGame = findViewById(R.id.btnFlashcard)
            multiChoice = findViewById(R.id.btnMultiple)
            btnTextGame = findViewById(R.id.btnTextGame)
            cardListView = findViewById(R.id.cardListView)
            leaderBoard = findViewById(R.id.leaderBoard)

            auth = FirebaseAuth.getInstance()

            leaderBoard.setOnClickListener {
                val intent = Intent(this, RankActivity::class.java)
                intent.putExtra("topicData", receivedTopic)
                startActivity(intent)
            }

            if (editable) {
                deleteButton.visibility = View.VISIBLE
                topicName.setOnClickListener {
                    receivedTopic!!.id?.let { it1 ->
                        showEditDialog("title",
                            it1, topicName.text.toString())
                    }
                }

                topicDescriptionView.setOnClickListener {
                    receivedTopic!!.id?.let { it1 ->
                        showEditDialog("description",
                            it1, topicDescriptionView.text.toString())
                    }
                }
            } else {
                deleteButton.visibility = View.GONE
            }

            username = receivedTopic!!.username.toString()
            val username = receivedTopic!!.username
            val topicNameText =  receivedTopic!!.title
            val topicDescription = receivedTopic!!.description
            val termNumberValue = receivedTopic!!.cards.size
            textToSpeech = TextToSpeech(this, this)

            topicName.text = topicNameText
            userName.text = username
            termNumber.text = termNumberValue.toString()
            topicDescriptionView.text = topicDescription

            closeButton.setOnClickListener { finish() }
            cardList = ArrayList(receivedTopic!!.cards.values)

            val adapter = CardAdapter(applicationContext, cardList, this)
            cardListView = findViewById(R.id.cardListView)
            cardListView.layoutManager = LinearLayoutManager(applicationContext)
            cardListView.adapter = adapter

            flashCardGame.setOnClickListener {
                val intent = Intent(this, FlashcardActivity::class.java)
                intent.putExtra("topicData", receivedTopic)
                startActivity(intent)
            }

            multiChoice.setOnClickListener {
                val intent = Intent(this, TestSettingActivity::class.java)
                intent.putExtra("username", receivedTopic!!.username)
                intent.putExtra("topicName", receivedTopic!!.title)
                intent.putExtra("cardList", ArrayList(cardList))
                intent.putExtra("topicId", receivedTopic!!.id)
                intent.putExtra("loginUser", loginUser)
                intent.putExtra("game", "multi")

                startActivity(intent)
            }

            btnTextGame.setOnClickListener {
                val intent = Intent(this, TestSettingActivity::class.java)
                intent.putExtra("username", receivedTopic!!.username)
                intent.putExtra("topicName", receivedTopic!!.title)
                intent.putExtra("cardList", ArrayList(cardList))
                intent.putExtra("game", "text")
                intent.putExtra("topicId", receivedTopic!!.id)
                intent.putExtra("loginUser", loginUser)
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

    private fun showEditDialog(field: String, topicId: String, currentValue: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Update Topic ${field.capitalize(Locale.ROOT)}")
            val inputField = EditText(this@TopicActivity)
            inputField.setText(currentValue)
            setView(inputField)
            setPositiveButton("Update") { dialog, _ ->
                val newValue = inputField.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    updateTopic(field, topicId, newValue)
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        }
        builder.show()
    }

    private fun updateTopic(field: String, topicId: String, newValue: String) {
        val topicReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics").child(topicId)
        val updateMap = hashMapOf<String, Any>(field to newValue)
        topicReference.updateChildren(updateMap)
            .addOnSuccessListener {
                showMessage("$field updated successfully")
                if (field == "Title") {
                    findViewById<TextView>(R.id.txtTopicName).text = newValue
                } else {
                    findViewById<TextView>(R.id.txtTopicDescription).text = newValue
                }
            }
            .addOnFailureListener { e ->
                showMessage("Failed to update $field: ${e.message}")
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

    override fun onItemLongClickListener(card: Card) {
        if (editable) {
            AlertDialog.Builder(this).apply {
                setTitle("Confirm Delete")
                setMessage("Are you sure you want to delete this card?")
                setPositiveButton("Delete") { dialog, which ->
                    deleteCardFromFirebase(card)
                }
                setNegativeButton("Cancel", null)
            }.create().show()
        }
    }

    private fun deleteCardFromFirebase(card: Card) {

        val topicId = receivedTopic?.id ?: return
        val cardIndex = cardList.indexOf(card)
        if (cardIndex == -1) return

        val cardReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("topics").child(topicId).child("cards").child(card.english)

        cardReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage("Card deleted successfully")
                cardList.removeAt(cardIndex)
                (cardListView.adapter as? CardAdapter)?.let {
                    it.notifyItemRemoved(cardIndex)
                    it.notifyItemRangeChanged(cardIndex, cardList.size)
                }
            } else {
                showMessage("Failed to delete card: ${task.exception?.message}")
            }
        }
    }

    private fun speakText(english: String) {
        textToSpeech.speak(english, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()

        super.onDestroy()
    }

    override fun onInit(status: Int) {
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