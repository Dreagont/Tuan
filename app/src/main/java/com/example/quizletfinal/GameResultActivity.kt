package com.example.quizletfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        val rightPercentage = 60f
        val wrongPercentage = 40f

        val circleChartView = findViewById<CircleChartView>(R.id.circleChartView)
        circleChartView.rightPercentage = rightPercentage
        circleChartView.wrongPercentage = wrongPercentage
    }
}