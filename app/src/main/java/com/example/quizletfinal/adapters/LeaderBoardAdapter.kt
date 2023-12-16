package com.example.quizletfinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener

class LeaderBoardAdapter(
    private val context: Context,
    private val dataList: MutableList<Pair<String, Long>>
) :
    RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewKey: TextView = itemView.findViewById(R.id.textViewKey)
        val rank: TextView = itemView.findViewById(R.id.rank)
        val textViewValue: TextView = itemView.findViewById(R.id.textViewValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val rank = position + 1

        holder.textViewKey.text = data.first
        holder.textViewValue.text = data.second.toString()
        holder.rank.text = rank.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

