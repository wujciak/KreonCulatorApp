package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

/**
 * Aktywność obsługująca ekran forum, umożliwiająca użytkownikom
 * wybór i przejście do jednego z dostępnych czatów forumowych.
 */
class ForumActivity : AppCompatActivity() {

    /**
     * Metoda onCreate inicjalizuje widok oraz ustawia akcje dla przycisków
     * umożliwiających przejście do odpowiednich czatów forumowych.
     *
     * @param savedInstanceState Stan zapisany z poprzednich uruchomień aktywności.
     */
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

    /**
     * Otwiera aktywność czatu dla określonego forum.
     *
     * @param forumId Identyfikator forum, które użytkownik chce otworzyć.
     */
    private fun openChatActivity(forumId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", forumId)
        startActivity(intent)
    }
}
