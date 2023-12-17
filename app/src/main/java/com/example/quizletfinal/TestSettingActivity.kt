package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.quizletfinal.models.Card

class TestSettingActivity : AppCompatActivity() {
    lateinit var btnInstant : Switch
    lateinit var btnSpeech : Switch
    lateinit var btnStartTest : Button
    lateinit var btnEnglish : Switch
    lateinit var btnClose : ImageView
    lateinit var txtTopicName : TextView
    lateinit var btnShuffle : Switch
    lateinit var testCount : EditText
    lateinit var txtMaxTopicTerm : TextView
    lateinit var currentSelect : TextView
    var loginUser: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_setting)
        btnInstant = findViewById(R.id.btnInstant)
        btnSpeech = findViewById(R.id.btnSpeech)
        btnStartTest = findViewById(R.id.btnStartTest)
        btnEnglish = findViewById(R.id.btnEnglish)
        btnClose = findViewById(R.id.btnClose)
        btnShuffle = findViewById(R.id.btnShuffle)
        txtTopicName = findViewById(R.id.txtTopicName)
        testCount = findViewById(R.id.testCount)
        txtMaxTopicTerm = findViewById(R.id.txtMaxTopicTerm)
        currentSelect = findViewById(R.id.currentSelect)

        var cardList =  intent.getSerializableExtra("cardList") as List<Card>
        var username = intent.getStringExtra("username")
        loginUser = intent.getStringExtra("loginUser")
        txtTopicName.text = intent.getStringExtra("topicName")

        txtMaxTopicTerm.text = " max " + cardList.size.toString()

        testCount.setText(cardList.size.toString())

        var isEnglish = if (btnEnglish.isChecked) 2 else 1
        var isInstant = if (btnInstant.isChecked) 1 else 2
        var isSpeech = if (btnSpeech.isChecked) 1 else 2
        var isShuffle = if (btnShuffle.isChecked) 1 else 2

        btnEnglish.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                currentSelect.text = "Definition"
                isEnglish =  2

            } else {
                currentSelect.text = "Term"
                isEnglish =  1
            }
        }
        btnInstant.setOnCheckedChangeListener { _, isChecked ->
            isInstant = if (isChecked) 1 else 2
        }

        btnSpeech.setOnCheckedChangeListener { _, isChecked ->
            isSpeech = if (isChecked) 1 else 2
        }

        btnShuffle.setOnCheckedChangeListener { _, isChecked ->
            isShuffle = if (isChecked) 1 else 2
        }

        var game = intent.getStringExtra("game")
        var topicId = intent.getStringExtra("topicId")


        btnClose.setOnClickListener{
            finish()
        }

        btnStartTest.setOnClickListener {
            if (isShuffle == 1) {
                cardList =  cardList.shuffled()
            }

            if (testCount.text.toString().toInt() > cardList.size || testCount.text.toString().toInt() < 1) {
                Toast.makeText(applicationContext, "Invalid length", Toast.LENGTH_SHORT).show()
            } else{
                cardList = cardList.take(testCount.text.toString().toInt())
                intent = Intent(this, if (game == "multi") MultiChoiceActivity::class.java else WrittenTestActivity::class.java)
                intent.putExtra("cardList",ArrayList(cardList))
                intent.putExtra("isEnglish",isEnglish)
                intent.putExtra("isInstant",isInstant)
                intent.putExtra("isSpeech",isSpeech)
                intent.putExtra("username",username)
                intent.putExtra("topicId",topicId)
                intent.putExtra("loginUser",loginUser)
                intent.putExtra("topicName",txtTopicName.text.toString())
                startActivity(intent)
                finish()
            }

        }
    }
}