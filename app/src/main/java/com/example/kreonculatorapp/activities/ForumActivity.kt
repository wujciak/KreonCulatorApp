package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class ForumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
        findViewById<Button>(R.id.forum1Button).setOnClickListener {
            openChatActivity("forum1")
        }
        findViewById<Button>(R.id.forum2Button).setOnClickListener {
            openChatActivity("forum2")
        }
        findViewById<Button>(R.id.forum3Button).setOnClickListener {
            openChatActivity("forum3")
        }
    }

    private fun openChatActivity(forumId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", forumId)
        startActivity(intent)
    }
}
