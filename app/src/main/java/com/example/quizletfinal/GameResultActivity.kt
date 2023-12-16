package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.quizletfinal.adapters.UserAnswerAdapter
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.UserAnswer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameResultActivity : AppCompatActivity() {
    lateinit var reDo : Button
    lateinit var reDoAll : Button
    private var wrongCardList: List<Card>? = null
    var topicId = ""
    var loginUser: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)
        reDo = findViewById(R.id.reDo)
        reDoAll = findViewById(R.id.reDoAll)

        var btnClose = findViewById<ImageView>(R.id.btnClose)

        btnClose.setOnClickListener { finish() }

        var username = intent.getStringExtra("username")

        var game = intent.getStringExtra("game")

        loginUser = intent.getStringExtra("loginUser")

        val selectedAnswers = intent.getStringArrayListExtra("selectedAnswers")
        val answerReviewList = intent.getIntArrayExtra("answerReviewList") ?: intArrayOf()
        val cardList = intent.getParcelableArrayListExtra<Card>("cardList")
        val correctCount = intent.getIntExtra("correctCount", 0)
        val incorrectCount = intent.getIntExtra("incorrectCount", 0)
        val totalTerm = intent.getIntExtra("totalTerm", 0)
        var name = intent.getStringExtra("topicName")
        wrongCardList = intent.getParcelableArrayListExtra("wrongCardList")

        if (wrongCardList.isNullOrEmpty()) {
            reDo.alpha = 0.5f
            reDo.isClickable = false
            reDo.isEnabled = false
        }

        reDo.setOnClickListener {
            val intent = Intent(this, TestSettingActivity::class.java)
            intent.putExtra("topicName", name)
            intent.putExtra("cardList", ArrayList(wrongCardList))
            intent.putExtra("game", game)
            intent.putExtra("username", username)
            intent.putExtra("topicId", topicId)
            intent.putExtra("loginUser", loginUser)
            startActivity(intent)
            finish()

        }

        reDoAll.setOnClickListener {
            val intent = Intent(this, TestSettingActivity::class.java)
            intent.putExtra("topicName", name)
            intent.putExtra("cardList", ArrayList(cardList))
            intent.putExtra("game", game)
            intent.putExtra("username", username)
            intent.putExtra("topicId", topicId)
            intent.putExtra("loginUser", loginUser)
            startActivity(intent)
            finish()
        }

        topicId = intent.getStringExtra("topicId").toString()

        saveResult(username.toString(),correctCount, if (game == "text") "Written" else "MultipleChoice")

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

    private fun saveResult(username: String, correctCount: Int, game : String) {

        val checkReference = FirebaseDatabase.getInstance().getReference("users")
            .child(username)
            .child("topics")
            .child(topicId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(game)) {
                        val readDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(username)
                            .child("topics")
                            .child(topicId)
                            .child(game)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach {userSnapshot ->
                                        if (userSnapshot.key.toString() == loginUser) {
                                            if (correctCount > userSnapshot.value.toString().toInt()) {
                                                val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                    .child(username)
                                                    .child("topics")
                                                    .child(topicId).child(game)
                                                    .child(loginUser.toString())
                                                    .setValue(correctCount).addOnSuccessListener {
                                                    }
                                            } else {

                                            }
                                        } else {
                                            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                .child(username)
                                                .child("topics")
                                                .child(topicId).child(game)
                                                .child(loginUser.toString())
                                                .setValue(correctCount).addOnSuccessListener {
                                                }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                    }
                    else {
                        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(username)
                            .child("topics")
                            .child(topicId).child(game)
                            .child(loginUser.toString())
                            .setValue(correctCount).addOnSuccessListener {
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }


}