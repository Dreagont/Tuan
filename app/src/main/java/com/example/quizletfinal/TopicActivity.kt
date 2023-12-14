package com.example.quizletfinal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizletfinal.models.Topic

class TopicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        // Retrieve the Topic data from the intent
        val receivedTopic = intent.getParcelableExtra("topic") as? Topic

        // Check if the data is not null
        if (receivedTopic != null) {
            // Handle the received Topic object here
            // For example, you can access its properties or display them
            val topicTitle = receivedTopic.title
            val topicDescription = receivedTopic.description

            // Display a Toast with the received data as an example
            val message = "Received Topic: $topicTitle - $topicDescription"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            // Handle the case where the Topic data is null
            Toast.makeText(this, "No Topic data received", Toast.LENGTH_SHORT).show()
        }
    }
}
