package com.example.quizletfinal

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    private lateinit var cardViewFront: CardView
    private lateinit var cardViewBack: CardView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var flipFrontAnimator: AnimatorSet
    private lateinit var flipBackAnimator: AnimatorSet
    private lateinit var frontCardText : TextView
    private lateinit var backCardText : TextView
    private var isFront: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        val receivedTopic = intent.getParcelableExtra<Topic>("topicData")
        cards = receivedTopic?.cards?.values?.toList() ?: emptyList()

        closeButton = findViewById(R.id.btnClose)
        indexTextView = findViewById(R.id.indexTerm)
        totalTextView = findViewById(R.id.totalTerm)
        cardViewFront = findViewById(R.id.cardViewFront)
        cardViewBack = findViewById(R.id.cardViewBack)

        frontCardText = findViewById(R.id.frontCard)
        backCardText = findViewById(R.id.backCard)

        backButton = findViewById(R.id.btnBack)
        playButton = findViewById(R.id.btnPlay)

        // Load the first card if exists and update UI accordingly
        if (cards.isNotEmpty()) {
            loadCard(cards[currentCardIndex])
        }

        val scale: Float = applicationContext.resources.displayMetrics.density
        cardViewFront.cameraDistance = 8000 * scale
        cardViewBack.cameraDistance = 8000 * scale

        flipFrontAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        flipBackAnimator = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        cardViewBack.visibility = View.GONE // Initially the back of the card is not visible

        cardViewFront.setOnClickListener {
            flipCard()
        }
        cardViewBack.setOnClickListener {
            flipCard()
        }

        backButton.setOnClickListener { previousCard() }
        playButton.setOnClickListener { flipCard() }
        closeButton.setOnClickListener { finish() }
    }

    private fun loadCard(card: Card) {
        frontCardText.text = card.english
        backCardText.text = card.vietnamese
        updateUI() // Update the index view and total view
    }

    private fun previousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--
            loadCard(cards[currentCardIndex])
        } else {
            Log.d("FlashcardActivity", "Reached first card")
        }
    }

    private fun nextCard() {
        if (currentCardIndex < cards.size - 1) {
            currentCardIndex++
            loadCard(cards[currentCardIndex])
        } else {
            Log.d("FlashcardActivity", "Reached last card")
        }
    }

    private fun flipCard() {
        if (isFront) {
            flipFrontAnimator.setTarget(cardViewFront)
            flipBackAnimator.setTarget(cardViewBack)
            flipFrontAnimator.start()
            flipBackAnimator.start()
            cardViewFront.visibility = View.GONE
            cardViewBack.visibility = View.VISIBLE
            isFront = false
        } else {
            flipFrontAnimator.setTarget(cardViewBack)
            flipBackAnimator.setTarget(cardViewFront)
            flipFrontAnimator.start()
            flipBackAnimator.start()
            cardViewBack.visibility = View.GONE
            cardViewFront.visibility = View.VISIBLE
            isFront = true
        }
    }

    private fun updateUI() {
        indexTextView.text = (currentCardIndex + 1).toString() // Updates the index text view with the current card index + 1
        totalTextView.text = cards.size.toString() // Updates the total text view with the number of cards
    }

}