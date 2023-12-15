package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.adapters.CardAdapter
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import java.util.Locale

class TopicActivity : AppCompatActivity(), OnItemClickListener, TextToSpeech.OnInitListener { //here
    private lateinit var textToSpeech: TextToSpeech //here
    var loginUser: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val receivedTopic = intent.getParcelableExtra<Topic>("topicData")
        loginUser = intent.getStringExtra("loginUser")

        if (receivedTopic != null) {
            val closeButton = findViewById<Button>(R.id.btnClose)
            val topicName = findViewById<TextView>(R.id.txtTopicName)
            val userName = findViewById<TextView>(R.id.txtUsername)
            val termNumber = findViewById<TextView>(R.id.txtTermNumber)
            val topicDescriptionView = findViewById<TextView>(R.id.txtTopicDescription)
            val flashCardGame = findViewById<LinearLayout>(R.id.btnFlashcard)
            val multiChoice = findViewById<LinearLayout>(R.id.btnMultiple)
            val btnTextGame = findViewById<LinearLayout>(R.id.btnTextGame)
            val cardListView = findViewById<RecyclerView>(R.id.cardListView)
            val leaderBoard = findViewById<TextView>(R.id.leaderBoard)

            leaderBoard.setOnClickListener {
                val intent = Intent(this, RankActivity::class.java)
                intent.putExtra("topicData", receivedTopic)
                startActivity(intent)
            }
            val username = receivedTopic.username
            val topicNameText = receivedTopic.title
            val topicDescription = receivedTopic.description
            val termNumberValue = receivedTopic.cards.size
            textToSpeech = TextToSpeech(this, this)

            topicName.text = topicNameText
            userName.text = username
            termNumber.text = termNumberValue.toString()
            topicDescriptionView.text = topicDescription

            closeButton.setOnClickListener { finish() }
            val cardList = receivedTopic.cards.values.toList()

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
                intent.putExtra("username",receivedTopic.username)
                intent.putExtra("topicName", receivedTopic.title)
                intent.putExtra("cardList", ArrayList(cardList))
                intent.putExtra("topicId",receivedTopic.id)
                intent.putExtra("loginUser",loginUser)
                intent.putExtra("game", "multi")

                startActivity(intent)
            }

            btnTextGame.setOnClickListener {
                val intent = Intent(this, TestSettingActivity::class.java)
                intent.putExtra("username",receivedTopic.username)
                intent.putExtra("topicName", receivedTopic.title)
                intent.putExtra("cardList", ArrayList(cardList))
                intent.putExtra("game", "text")
                intent.putExtra("topicId",receivedTopic.id)
                intent.putExtra("loginUser",loginUser)
                startActivity(intent)
            }


        } else {
            Toast.makeText(this, "No Topic data received", Toast.LENGTH_SHORT).show()
        }
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

