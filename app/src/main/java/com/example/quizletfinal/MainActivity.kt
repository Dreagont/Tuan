package com.example.quizletfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var mainFragment: MainFragment
    private lateinit var topicFragment: TopicFragment
    private lateinit var libraryFragment: LibraryFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var activeFragment: Fragment
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment()
        topicFragment = TopicFragment()
        libraryFragment = LibraryFragment()
        profileFragment = ProfileFragment()
        activeFragment = mainFragment
        fragmentManager = supportFragmentManager

        val username: String? = FirebaseAuth.getInstance().currentUser?.email

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
            intent.putExtra("username", FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0))
            startActivity(intent)
            dialog.dismiss()
        }

        addTopic?.setOnClickListener {
            val intent = Intent(this, AddTopicActivity::class.java)
            intent.putExtra("username", FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0))
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


}