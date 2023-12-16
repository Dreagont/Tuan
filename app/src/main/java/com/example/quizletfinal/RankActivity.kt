package com.example.quizletfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.quizletfinal.adapters.ViewPageAdapter
import com.example.quizletfinal.models.Topic
import com.google.android.material.tabs.TabLayout

class RankActivity : AppCompatActivity() {
    lateinit var tableLayout : TabLayout
    lateinit var viewPage: ViewPager
    private var topic: Topic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        topic = intent.getParcelableExtra("topicData")

        tableLayout = findViewById(R.id.table)
        viewPage = findViewById(R.id.page)

        tableLayout.setupWithViewPager(viewPage)
        var adapter = ViewPageAdapter(supportFragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(MultiChoiceRankFragment.newInstance(topic?.id.toString(),topic?.username.toString()), "Multi Choice")
        adapter.addFragment(WrittenRankFragment.newInstance(topic?.id.toString(),topic?.username.toString()), "Written")

        viewPage.adapter = adapter

        var btnClose = findViewById<ImageView>(R.id.btnClose)

        btnClose.setOnClickListener {
            finish()
        }

        var txtTopicName = findViewById<TextView>(R.id.txtTopicName)
        txtTopicName.text = topic?.title ?: ""
    }
}