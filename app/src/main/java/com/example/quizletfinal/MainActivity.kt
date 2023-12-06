package com.example.quizletfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.quizletfinal.TopicFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            }
            true
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