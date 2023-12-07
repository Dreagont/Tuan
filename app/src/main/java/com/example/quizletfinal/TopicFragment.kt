package com.example.quizletfinal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

class TopicFragment : Fragment() {
    private lateinit var searchBar: SearchView
    private lateinit var myTopicList: RecyclerView
    private lateinit var otherTopicList: RecyclerView
    private lateinit var btnOpenAddTopic: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBar = view.findViewById(R.id.search_bar)
        myTopicList = view.findViewById(R.id.myTopicList)
        otherTopicList = view.findViewById(R.id.otherTopicList)
        btnOpenAddTopic = view.findViewById(R.id.btnOpenAddTopic)

        btnOpenAddTopic.setOnClickListener {
            activity?.let {
                startActivity(Intent(it, AddFolderActivity::class.java))
            }
        }
    }
}