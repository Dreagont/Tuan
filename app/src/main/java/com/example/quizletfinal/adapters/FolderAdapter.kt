package com.example.quizletfinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R // Replace with the actual package name
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener

class FolderAdapter(private val context: Context, private val folderList: List<Folder>,private var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtFolderName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.folder_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folderList[position]
        holder.titleTextView.text = folder.title
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClickListener(folder)
        }
    }

    override fun getItemCount(): Int {
        return folderList.size
    }
}
