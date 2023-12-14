package com.example.quizletfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.adapters.TopicAdapter
import com.example.quizletfinal.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopicFragment : Fragment() {
    private lateinit var searchBar: SearchView
    private lateinit var myTopicList: RecyclerView
    private lateinit var otherTopicList: RecyclerView
    private lateinit var btnOpenAddTopic: Button
    private lateinit var ifNoTopic : LinearLayout
    val topics = mutableListOf<Topic>()

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
        ifNoTopic = view.findViewById(R.id.ifNoTopic)

        ifNoTopic.visibility = View.GONE

        otherTopicList.layoutManager = LinearLayoutManager(requireContext())
        myTopicList.layoutManager = LinearLayoutManager(requireContext())
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("Username", "No Username")
        val email = sharedPreferences.getString("Email", "No Email")


        if (email != null) {
            loadTopic(email)
        };

        btnOpenAddTopic.setOnClickListener {
            activity?.let {
                startActivity(Intent(it, AddFolderActivity::class.java))
            }
        }
    }
    private fun readWithEmail(email: String?, processSnapshot: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    processSnapshot(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

    private fun readTopic(key: String, processSnapshot: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference("users").child(key)
            .child("topics")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    processSnapshot(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

    private fun loadTopic(email: String) {
        readWithEmail(email) { dataSnapshot ->
            dataSnapshot.children.forEach { userSnapshot ->
                if (userSnapshot.child("email").value.toString() == email) {
                    readTopic(userSnapshot.key.toString()) { dataSnapshot ->
                        val updatedTopics = mutableListOf<Topic>()

                        dataSnapshot.children.forEach { topicSnapshot ->
                            val topic = topicSnapshot.getValue(Topic::class.java)
                            if (topic != null) {
                                updatedTopics.add(topic)
                            }
                        }

                        topics.clear()
                        topics.addAll(updatedTopics)

                        val adapter = TopicAdapter(topics, requireContext())
                        myTopicList.adapter = adapter
                        adapter.notifyDataSetChanged()

                        if (topics.isEmpty()) {
                            ifNoTopic.visibility = View.VISIBLE
                        } else {
                            ifNoTopic.visibility = View.GONE
                        }
                    }
                    return@readWithEmail
                }
            }
        }
    }

}