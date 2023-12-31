package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.quizletfinal.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var mainFragment: MainFragment
    private lateinit var topicFragment: TopicFragment
    private lateinit var libraryFragment: LibraryFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var activeFragment: Fragment
    private lateinit var fragmentManager: FragmentManager
    var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment()
        topicFragment = TopicFragment()
        libraryFragment = LibraryFragment()
        profileFragment = ProfileFragment()
        activeFragment = mainFragment
        fragmentManager = supportFragmentManager

        username = FirebaseAuth.getInstance().currentUser?.email.toString()



        val userEmail = FirebaseAuth.getInstance().currentUser?.email


        readWithEmail(userEmail, object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (userSnapshot in dataSnapshot.children) {
                    val userEmailK = userSnapshot.child("email").value.toString()

                    if (userEmail == userEmailK) {
                        username = userSnapshot.child("username").value.toString()
                        with(getSharedPreferences("UserDetails", MODE_PRIVATE).edit()) {
                            putString("username", username)
                            putString("loginUsername",userSnapshot.child("username").value.toString())
                            putString("image",userSnapshot.child("profileImage").value.toString())
                            apply()
                        }
                        break
                    }
                }
                if (username != null) {
                    Log.d("FirebaseData", username)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException())
            }
        })
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, mainFragment)
        transaction.commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomBar)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeTab -> switchFragment(mainFragment)
                R.id.topicTab -> switchFragment(topicFragment)
                R.id.libTab -> switchFragment(libraryFragment)
                R.id.profileTab -> switchFragment(profileFragment)
                R.id.add -> showAddDialog()
            }
            true
        }
    }

    private fun showAddDialog() {
        val view: View = layoutInflater.inflate(R.layout.add_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()

        val addFolder = dialog.findViewById<Button>(R.id.btnAddFolder)
        val addTopic = dialog.findViewById<Button>(R.id.btnAddTopic)

        addFolder?.setOnClickListener {
            val intent = Intent(this, AddFolderActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            dialog.dismiss()
        }

        addTopic?.setOnClickListener {
            val intent = Intent(this, AddTopicActivity::class.java)
            intent.putExtra("username",username )
            startActivity(intent)
            dialog.dismiss()
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            transaction.hide(activeFragment).add(R.id.mainFrame, fragment).commit()
        } else {
            transaction.hide(activeFragment).show(fragment).commit()
        }
        activeFragment = fragment
    }

    fun readWithEmail(currentMail: String?, valueEventListener: ValueEventListener) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.addListenerForSingleValueEvent(valueEventListener)
    }


}