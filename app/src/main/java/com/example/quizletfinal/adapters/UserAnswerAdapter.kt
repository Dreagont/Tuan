package com.example.quizletfinal.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.UserAnswer

class UserAnswerAdapter(private val context: Context, private val userAnswers: List<UserAnswer>) : BaseAdapter() {

    override fun getCount(): Int {
        return userAnswers.size
    }

    override fun getItem(position: Int): Any {
        return userAnswers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rowView: View

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.card_review_layout, parent, false)
            holder = ViewHolder(rowView)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }

        val userAnswer = userAnswers[position]

        holder.txtWordFront.text = userAnswer.english
        holder.txtCorrectAnswer.text = userAnswer.vietnamese
        holder.txtInCorrectAnswer.text = userAnswer.selectedAnswer
        holder.txtIsCorrect.text = if (userAnswer.isCorrect) "Correct" else "Incorrect"
        holder.ifCorrect.visibility = if (userAnswer.isCorrect) View.GONE else View.VISIBLE
        holder.txtIsCorrect.setTextColor(
            if (userAnswer.isCorrect)
                context.resources.getColor(android.R.color.black)
            else
                context.resources.getColor(android.R.color.white)
        )
        holder.isCorrectBackground.setBackgroundColor(if (userAnswer.isCorrect) Color.GREEN else Color.RED)

        return rowView
    }

    private class ViewHolder(view: View) {
        val txtWordFront: TextView = view.findViewById(R.id.txtWordFront)
        val txtCorrectAnswer: TextView = view.findViewById(R.id.txtCorrectAnswer)
        val txtInCorrectAnswer: TextView = view.findViewById(R.id.txtInCorrectAnswer)
        val txtIsCorrect: TextView = view.findViewById(R.id.txtIsCorrect)
        val isCorrectBackground: LinearLayout = view.findViewById(R.id.isCorrectBackground)
        val ifCorrect: LinearLayout = view.findViewById(R.id.ifCorrect)
    }
}
