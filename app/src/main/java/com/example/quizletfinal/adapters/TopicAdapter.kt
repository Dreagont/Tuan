package com.example.quizletfinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.Topic

class TopicAdapter(
    private var topics : List<Topic>,
    private val context: Context
) : RecyclerView.Adapter<TopicAdapter.CertificatesHolder>() {

    private var originalTopics: List<Topic> = topics
    private var adapterViewListener: AdapterView.OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(user: Topic)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        adapterViewListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listener.onItemClick(topics[position])
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificatesHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.topic_layout, parent, false)
        return CertificatesHolder(view)
    }

    override fun onBindViewHolder(holder: CertificatesHolder, position: Int) {
        val topic = topics[position]
        holder.txtTopicName.text = topic.title


    }

    override fun getItemCount(): Int {
        return topics.size
    }

    inner class CertificatesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTopicName: TextView = itemView.findViewById(R.id.txtTopicName)

        init {
            itemView.setOnClickListener {
                adapterViewListener?.onItemClick(null, itemView, adapterPosition, itemId)
            }
        }
    }
}
