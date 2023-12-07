package com.example.quizletfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val items: MutableList<CardItem>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordNameEditText: EditText = view.findViewById(R.id.txtWordName)
        val wordMeaningEditText: EditText = view.findViewById(R.id.txtWordMean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topic_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.wordNameEditText.setText(item.wordName)
        holder.wordMeaningEditText.setText(item.wordMeaning)
    }

    override fun getItemCount() = items.size

    fun addItem(item: CardItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}
