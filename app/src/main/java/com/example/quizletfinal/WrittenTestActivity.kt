package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.quizletfinal.models.Card
import java.util.Locale

class WrittenTestActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var cardTerm : TextView
    lateinit var txtUserInput : EditText
    lateinit var btnSkip : TextView
    lateinit var listLenght: TextView
    private var correctAnswer: String? = null
    private var correctCount: Int = 0
    private var inCorrectCount: Int = 0;
    private var currentIndex = 0
    private var answerReviewList: MutableList<Int> = mutableListOf()
    private var selectedAnswers: MutableList<String?> = mutableListOf()
    lateinit var cardList : List<Card>
    private lateinit var textToSpeech: TextToSpeech
    private var isEnglish = 3;
    private var isInstant = 3
    private var isSpeech = 3
    private var wrongCardList: MutableList<Card> = mutableListOf()
    private var username = "22"
    var topicId = ""
    var loginUser: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_written_test)

        cardList = intent.getSerializableExtra("cardList") as ArrayList<Card>

        cardTerm = findViewById(R.id.cardTerm)
        txtUserInput = findViewById(R.id.txtUserInput)
        btnSkip = findViewById(R.id.btnSkip)
        listLenght = findViewById(R.id.totalTerm)

        listLenght.text = cardList.size.toString()

        textToSpeech = TextToSpeech(this, this)

        isEnglish = intent.getIntExtra("isEnglish",3)
        isInstant = intent.getIntExtra("isInstant",3)
        isSpeech = intent.getIntExtra("isSpeech",3)
        username = intent.getStringExtra("username").toString()
        topicId = intent.getStringExtra("topicId").toString()
        loginUser = intent.getStringExtra("loginUser")

        loadTerm(cardList[currentIndex])


        txtUserInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val userInput = txtUserInput.text.toString().trim()
                if (userInput.isNotEmpty()) {
                    validateAnswer(userInput, cardList[currentIndex])
                    moveToNextCard(cardList)
                    txtUserInput.text.clear()
                }
                return@setOnKeyListener true // Consume the event
            }
            false // Let the event be handled normally
        }

        btnSkip.setOnClickListener {
            answerReviewList.add(0)
            inCorrectCount++
            selectedAnswers.add("")
            moveToNextCard(cardList)
        }

    }
    private fun validateAnswer(text: CharSequence?, card: Card) {
        if (text != null) {
            val isCorrect = text.toString().equals(correctAnswer, ignoreCase = true)
            if (isSpeech == 1)  {
                if (isEnglish == 1) {
                    speakText(card.english)
                } else {
                    speakText(card.vietnamese)
                }
            }
            if (isCorrect) {
                answerReviewList.add(1)
                correctCount ++
            } else {
                wrongCardList.add(card)
                answerReviewList.add(0)
                inCorrectCount ++;
            }
            selectedAnswers.add(text.toString())
            if (isInstant == 1) {
                Toast.makeText(
                    applicationContext,
                    if (isCorrect) "Correct" else "Incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
    fun moveToNextCard(cardList: List<Card>) {
        currentIndex++

        if (currentIndex < cardList.size) {
            loadTerm(cardList[currentIndex])
            findViewById<TextView>(R.id.indexTerm).text = (currentIndex + 1).toString()
        } else {
            var name = intent.getStringExtra("topicName")
            val intent = Intent(this, GameResultActivity::class.java)

            val totalTermText = findViewById<TextView>(R.id.totalTerm).text.toString()
            val totalTerm = totalTermText.toIntOrNull() ?: 0
            intent.putExtra("answerReviewList", answerReviewList.toIntArray())
            intent.putExtra("correctCount", correctCount)
            intent.putExtra("incorrectCount", inCorrectCount)
            intent.putExtra("totalTerm", totalTerm)
            intent.putExtra("topicName", name)
            intent.putExtra("username",username)
            intent.putExtra("topicId",topicId)
            intent.putExtra("loginUser",loginUser)
            intent.putParcelableArrayListExtra("wrongCardList", ArrayList(wrongCardList))
            intent.putStringArrayListExtra("selectedAnswers", ArrayList(selectedAnswers))
            intent.putParcelableArrayListExtra("cardList", ArrayList(cardList))
            intent.putExtra("game", "text")

            startActivity(intent)
            finish()
        }
    }

    private fun loadTerm(card: Card) {
        cardTerm.text = card.vietnamese
        correctAnswer = card.english
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

}