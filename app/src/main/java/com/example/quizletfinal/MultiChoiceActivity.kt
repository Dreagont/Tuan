package com.example.quizletfinal

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.quizletfinal.models.Card
import java.util.Locale

class MultiChoiceActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var frontAnimator  : AnimatorSet
    private lateinit var backAnimator : AnimatorSet
    private lateinit var btnBackCard : CardView
    private lateinit var btnFrontCard : CardView
    private var isFront : Boolean  = true
    private lateinit var btnAnswerA : CardView
    private lateinit var btnAnswerB : CardView
    private lateinit var btnAnswerC : CardView
    private lateinit var btnAnswerD : CardView
    private lateinit var txtAnswerA : TextView
    private lateinit var txtAnswerB : TextView
    private lateinit var txtAnswerC : TextView
    private lateinit var txtAnswerD : TextView
    private lateinit var frontCard : TextView
    private lateinit var backCard : TextView
    private var currentIndex = 0
    private var correctAnswer: String? = null
    private var answerReviewList: MutableList<Int> = mutableListOf()
    private var selectedAnswers: MutableList<String?> = mutableListOf()
    private var correctCount: Int = 0
    private var inCorrectCount: Int = 0;
    private lateinit var textToSpeech: TextToSpeech

    private var cardList = listOf(
        Card("Hello", "Xin chào"),
        Card("Goodbye", "Tạm biệt"),
        Card("Friend", "Bạn bè"),
        Card("Family", "Gia đình"),
        Card("Love", "Tình yêu"),
        Card("Food", "Đồ ăn"),
        Card("Water", "Nước"),
        Card("Book", "Sách"),
        Card("Movie", "Phim"),
        Card("Music", "Âm nhạc"),
        Card("Computer", "Máy tính"),
        Card("Programming", "Lập trình"),
        Card("Travel", "Du lịch"),
        Card("Nature", "Thiên nhiên"),
        Card("City", "Thành phố"),
        Card("Education", "Giáo dục"),
        Card("Work", "Công việc"),
        Card("Hobby", "Sở thích"),
        Card("Exercise", "Tập thể dục"),
        Card("Sleep", "Ngủ")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_choice)
        cardList = cardList.shuffled()



        var scale : Float = applicationContext.resources.displayMetrics.density

        btnFrontCard = findViewById(R.id.btnFrontCard)
        btnBackCard = findViewById(R.id.btnBackCard)

        frontCard = findViewById(R.id.frontCard)
        backCard = findViewById(R.id.backCard)

        btnAnswerA = findViewById(R.id.btnAnswerA)
        btnAnswerB = findViewById(R.id.btnAnswerB)
        btnAnswerC = findViewById(R.id.btnAnswerC)
        btnAnswerD = findViewById(R.id.btnAnswerD)

        txtAnswerA = findViewById(R.id.txtAnswerA)
        txtAnswerB = findViewById(R.id.txtAnswerB)
        txtAnswerC = findViewById(R.id.txtAnswerC)
        txtAnswerD = findViewById(R.id.txtAnswerD)

        findViewById<TextView>(R.id.totalTerm).text = cardList.size.toString()
        textToSpeech = TextToSpeech(this, this)

        btnFrontCard.cameraDistance = 8000 * scale;
        btnBackCard.cameraDistance = 8000 * scale;

        frontAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        backAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        btnFrontCard.setOnClickListener {
            flip()
        }

        btnBackCard.setOnClickListener {
            flip()
        }

        loadTerm(cardList[currentIndex])

        btnAnswerA.setOnClickListener {
            validateAnswer(txtAnswerA.text, cardList[currentIndex])
            moveToNextCard(cardList)
        }

        btnAnswerB.setOnClickListener {
            validateAnswer(txtAnswerB.text, cardList[currentIndex])
            moveToNextCard(cardList)
        }

        btnAnswerC.setOnClickListener {
            validateAnswer(txtAnswerC.text, cardList[currentIndex])
            moveToNextCard(cardList)
        }

        btnAnswerD.setOnClickListener {
            validateAnswer(txtAnswerD.text, cardList[currentIndex])
            moveToNextCard(cardList)
        }


    }

    private fun validateAnswer(text: CharSequence?, card: Card) {
        if (text != null) {
            val isCorrect = text == correctAnswer

            if (isCorrect) {
                answerReviewList.add(1)
                correctCount ++;
                speakText(card.english)
            } else {
                answerReviewList.add(0)
                inCorrectCount ++;
            }
            selectedAnswers.add(text.toString())

            Toast.makeText(
                applicationContext,
                if (isCorrect) "Correct" else "Incorrect",
                Toast.LENGTH_SHORT
            ).show()
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
    private fun flip() {
        if (isFront) {
            frontAnimator.setTarget(btnFrontCard)
            backAnimator.setTarget(btnBackCard)
            frontAnimator.start()
            backAnimator.start()
            isFront =false
        } else {
            frontAnimator.setTarget(btnBackCard)
            backAnimator.setTarget(btnFrontCard)
            frontAnimator.start()
            backAnimator.start()
            isFront = true
        }
    }

    fun moveToNextCard(cardList: List<Card>) {
        currentIndex++

        if (currentIndex < cardList.size) {
            loadTerm(cardList[currentIndex])
            findViewById<TextView>(R.id.indexTerm).text = (currentIndex + 1).toString()
        } else {
            val intent = Intent(this, GameResultActivity::class.java)

            val totalTermText = findViewById<TextView>(R.id.totalTerm).text.toString()
            val totalTerm = totalTermText.toIntOrNull() ?: 0

            intent.putExtra("answerReviewList", answerReviewList.toIntArray())
            intent.putExtra("correctCount", correctCount)
            intent.putExtra("incorrectCount", inCorrectCount)
            intent.putExtra("totalTerm", totalTerm)
            intent.putStringArrayListExtra("selectedAnswers", ArrayList(selectedAnswers))
            intent.putParcelableArrayListExtra("cardList", ArrayList(cardList))
            startActivity(intent)
        }
    }

    private fun loadTerm(card: Card) {
        frontCard.text = card.english
        backCard.text = card.vietnamese

        val incorrectAnswers = cardList
            .filter { it != card }
            .shuffled()
            .take(3)
            .toMutableList()

        val correctAnswerPosition = (0..3).random()

        // Populate the answer options
        val answerOptions = mutableListOf<String>()
        for (i in 0 until 4) {
            val answer = if (i == correctAnswerPosition) {
                card.vietnamese
            } else {
                val incorrectAnswer = incorrectAnswers.removeAt(0)
                incorrectAnswer.vietnamese
            }
            answerOptions.add(answer)
        }

        val shuffledOptions = answerOptions.shuffled()

        txtAnswerA.text = shuffledOptions[0]
        txtAnswerB.text = shuffledOptions[1]
        txtAnswerC.text = shuffledOptions[2]
        txtAnswerD.text = shuffledOptions[3]

        correctAnswer = card.vietnamese
    }

    fun <T> AppCompatActivity.startActivity(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
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