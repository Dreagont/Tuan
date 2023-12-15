package com.example.quizletfinal

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Topic

class FlashcardActivity : AppCompatActivity() {
    private lateinit var cards: List<Card>
    private var currentCardIndex = 0
    private lateinit var closeButton: Button
    private lateinit var indexTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var cardView: CardView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var frontAnimator: AnimatorSet
    private lateinit var backAnimator: AnimatorSet
    private lateinit var btnBackCard : LinearLayout
    private lateinit var btnFrontCard : LinearLayout
    private lateinit var frontCard : TextView
    private lateinit var backCard : TextView
    private var isFront: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        val receivedTopic = intent.getParcelableExtra<Topic>("topicData")

        cards = receivedTopic?.cards?.values?.toList() ?: emptyList()

        closeButton = findViewById(R.id.btnClose)
        indexTextView = findViewById(R.id.indexTerm)
        totalTextView = findViewById(R.id.totalTerm)
        cardView = findViewById(R.id.cardView)

        var scale : Float = applicationContext.resources.displayMetrics.density

        btnFrontCard = findViewById(R.id.frontCardLayout)
        btnBackCard = findViewById(R.id.backCardLayout)

        frontCard = findViewById(R.id.frontCard)
        backCard = findViewById(R.id.backCard)

        backButton = findViewById(R.id.btnBack)
        playButton = findViewById(R.id.btnPlay)

        btnFrontCard.cameraDistance = 8000 * scale;
        btnBackCard.cameraDistance = 8000 * scale;

        frontAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        backAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        loadTerm(cards[currentCardIndex])

        btnFrontCard.setOnClickListener {
            flipCard()
        }
        btnBackCard.setOnClickListener {
            flipCard()
        }

        backButton.setOnClickListener { previousCard() }
        playButton.setOnClickListener { flipCard() }


        closeButton.setOnClickListener { finish() }
    }

    private fun loadTerm(card: Card) {
        frontCard.text = card.english
        backCard.text = card.vietnamese
    }

    private fun previousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--
        } else {
            Log.d("FlashcardActivity", "Reached first card")
        }
    }

    private fun nextCard() {
        if (currentCardIndex < cards.size - 1) {
            currentCardIndex++
        } else {
            Log.d("FlashcardActivity", "Reached last card")
        }
    }

    private fun flipCard() {
        if (isFront) {
            btnFrontCard.visibility = View.GONE
            btnBackCard.visibility = View.VISIBLE
            frontAnimator.setTarget(btnFrontCard)
            backAnimator.setTarget(btnBackCard)
            frontAnimator.start()
            backAnimator.start()
            isFront =false
        } else {
            btnFrontCard.visibility = View.VISIBLE
            btnBackCard.visibility = View.GONE
            frontAnimator.setTarget(btnBackCard)
            backAnimator.setTarget(btnFrontCard)
            frontAnimator.start()
            backAnimator.start()
            isFront = true
        }
    }

    private fun markCardLearned() {
        // Example: cards[currentCardIndex].isLearned = true
        nextCard()
    }

    private fun markCardLearnLater() {
        // Example: cards[currentCardIndex].isLearned = false
        nextCard()
    }


}