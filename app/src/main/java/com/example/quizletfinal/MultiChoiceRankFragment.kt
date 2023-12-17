package com.example.quizletfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.adapters.LeaderBoardAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MultiChoiceRankFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var multiRankList : RecyclerView
    val dataList = mutableListOf<Pair<String, Long>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_multi_choice_rank, container, false)
        multiRankList = view.findViewById(R.id.multiRankList)
        multiRankList.layoutManager = LinearLayoutManager(requireContext())
        val id = arguments?.getString(ARG_PARAM1)
        val username = arguments?.getString(ARG_PARAM2)

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(username.toString())
            .child("topics")
            .child(id.toString())
            .child("MultipleChoice")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key

                    val value = childSnapshot.value as Long
                    dataList.add(Pair(key!!, value))
                }

                dataList.sortByDescending { it.second }
                val adapter = LeaderBoardAdapter(requireContext(),dataList)
                multiRankList.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MultiChoiceRankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}