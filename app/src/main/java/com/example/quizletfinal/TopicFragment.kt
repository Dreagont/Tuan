package com.example.quizletfinal

import TopicAdapter
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.models.Card
import com.example.quizletfinal.models.Folder
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopicFragment : Fragment() ,OnItemClickListener {
    private lateinit var searchBar: SearchView
    private lateinit var myTopicList: RecyclerView
    private lateinit var btnOpenAddTopic: Button
    private lateinit var ifNoTopic : LinearLayout
    private lateinit var progressDialog: ProgressDialog
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var username: String
    var loginUser: String? = ""
    val topics = mutableListOf<Topic>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)



        searchBar = view.findViewById(R.id.search_bar)
        myTopicList = view.findViewById(R.id.myTopicList)
        btnOpenAddTopic = view.findViewById(R.id.btnOpenAddTopic)
        ifNoTopic = view.findViewById(R.id.ifNoTopic)

        ifNoTopic.visibility = View.GONE

        myTopicList.layoutManager = LinearLayoutManager(requireContext())
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "No Username") ?: "No Username"
        loginUser = sharedPreferences.getString("loginUsername", "No Username")


        loadTopic(username)

        btnOpenAddTopic.setOnClickListener {
            activity?.let {
                startActivity(Intent(it, AddTopicActivity::class.java))
            }
        }

        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (myTopicList.adapter as? TopicAdapter)?.filter?.filter(newText)
                return true
            }
        })
    }

    private fun loadTopic(username: String) {
        progressDialog.show()

        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val updatedTopics = mutableListOf<Topic>()

                    dataSnapshot.children.forEach { userSnapshot ->
                        val user = userSnapshot.child("username").value.toString()
                        if (user == username) {
                            userSnapshot.child("topics").children.forEach { topicSnapshot ->
                                val topic = topicSnapshot.getValue(Topic::class.java)
                                topic?.let { updatedTopics.add(it) }
                            }
                        }
                    }
                    topics.clear()
                    topics.addAll(updatedTopics)

                    val adapter = TopicAdapter(requireContext(), topics, this@TopicFragment)
                    myTopicList.adapter = adapter
                    adapter.notifyDataSetChanged()

                    ifNoTopic.visibility = if (topics.isEmpty()) View.VISIBLE else View.GONE
                    handler.post { progressDialog.dismiss() }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "No Username")
        if (username != null) {
            loadTopic(username)
        }
    }

    override fun onItemClickListener(topic: Topic) {
        val intent = Intent(requireContext(), TopicActivity::class.java)
        intent.putExtra("topicData", topic)
        intent.putExtra("loginUser",loginUser)
        intent.putExtra("editable", true)
        intent.putExtra("fromTopicFragment", true)
        startActivity(intent)
    }

    override fun onItemClickListener(folder: Folder) {
        TODO("Not yet implemented")
    }

    override fun onItemClickListener(card: Card) {
        TODO("Not yet implemented")
    }

    override fun onItemLongClickListener(card: Card) {
        TODO("Not yet implemented")
    }

    override fun onItemDeleteListener(topic: Topic) {
        TODO("Not yet implemented")
    }

    override fun onItemMoveListener(topic: Topic) {
        TODO("Not yet implemented")
    }

}