package com.example.quizletfinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic // Renamed from Folder

class TopicAdapter(
    private val context: Context,
    private val topicList: List<Topic>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtTopicName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.topic_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topicList[position]
        holder.titleTextView.text = topic.title
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClickListener(topic)
        }
    }

    override fun getItemCount(): Int {
        return topicList.size
    }
}