package com.example.quizletfinal.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.OnItemClickListener

class CardAdapter(private val context: Context,
                  private val cardList: List<Card>,
                  private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frontCard: TextView = itemView.findViewById(R.id.frontCard)
        val backCard: TextView = itemView.findViewById(R.id.backCard)

        private var isFront: Boolean = true
        private lateinit var frontAnimator: AnimatorSet
        private lateinit var backAnimator: AnimatorSet

        init {
            setupAnimations(itemView.context)
            setupClickListener()
        }

        private fun setupAnimations(context: Context) {
            val scale: Float = context.resources.displayMetrics.density
            itemView.cameraDistance = 8000 * scale

            frontAnimator = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
            backAnimator = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
        }

        private fun setupClickListener() {
            itemView.setOnClickListener {
                flip()
            }
        }

        private fun flip() {
            if (isFront) {
                backAnimator.setTarget(itemView)
                backAnimator.start()
                isFront = false
            } else {
                frontAnimator.setTarget(itemView)
                frontAnimator.start()
                isFront = true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.word_display_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cardList[position]
        holder.frontCard.text = card.english
        holder.backCard.text = card.vietnamese
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClickListener(card)
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}