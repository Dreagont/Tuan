package com.example.quizletfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.adapters.UserAnswerAdapter
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.UserAnswer

class GameResultActivity : AppCompatActivity() {
    lateinit var reDo : Button
    lateinit var reDoAll : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        val selectedAnswers = intent.getStringArrayListExtra("selectedAnswers")
        val answerReviewList = intent.getIntArrayExtra("answerReviewList") ?: intArrayOf()
        val cardList = intent.getParcelableArrayListExtra<Card>("cardList")
        val correctCount = intent.getIntExtra("correctCount", 0)
        val incorrectCount = intent.getIntExtra("incorrectCount", 0)
        val totalTerm = intent.getIntExtra("totalTerm", 0)


        val rightPercentage: Float = correctCount.toFloat() / totalTerm.toFloat() * 100
        val wrongPercentage: Float = incorrectCount.toFloat() / totalTerm.toFloat() * 100

        val circleChartView = findViewById<CircleChartView>(R.id.circleChartView)
        circleChartView.rightPercentage = rightPercentage
        circleChartView.wrongPercentage = wrongPercentage

        findViewById<TextView>(R.id.txtCorrectCount).setText(correctCount.toString())
        findViewById<TextView>(R.id.txtIncorrectCount).setText(incorrectCount.toString())
        val userAnswersList = mutableListOf<UserAnswer>()


        for (i in 0 until totalTerm) {
            val selectedAnswer = selectedAnswers?.get(i) ?: ""
            val answerReview = answerReviewList.getOrNull(i) ?: 0

            val isCorrect = answerReview == 1
            val userAnswer = UserAnswer(isCorrect, selectedAnswer, cardList?.get(i)?.english ?: "", cardList?.get(i)?.vietnamese ?: "")
            userAnswersList.add(userAnswer)
        }
        val listView = findViewById<ListView>(R.id.reviewList)
        val adapter = UserAnswerAdapter(this, userAnswersList)
        listView.adapter = adapter

        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (adapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()

    }
}