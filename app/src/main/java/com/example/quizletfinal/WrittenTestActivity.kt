package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.quizletfinal.models.Card

class WrittenTestActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_written_test)

        cardTerm = findViewById(R.id.cardTerm)
        txtUserInput = findViewById(R.id.txtUserInput)
        btnSkip = findViewById(R.id.btnSkip)
        listLenght = findViewById(R.id.totalTerm)

        listLenght.text = cardList.size.toString()

        cardList = cardList.shuffled()
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

            if (isCorrect) {
                answerReviewList.add(1)
                correctCount++
            } else {
                answerReviewList.add(0)
                inCorrectCount++
            }
            selectedAnswers.add(text.toString())

            Toast.makeText(
                applicationContext,
                if (isCorrect) "Correct" else "Incorrect",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun moveToNextCard(cardList: List<Card>) {
        currentIndex++

        if (currentIndex < cardList.size) {
            loadTerm(cardList[currentIndex])
            findViewById<TextView>(R.id.indexTerm).text = (currentIndex + 1).toString()
        } else {
            val intent = Intent(this, GameResultActivity::class.java)

            val totalTermText = listLenght.text.toString()
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
        cardTerm.text = card.vietnamese
        correctAnswer = card.english
    }

}