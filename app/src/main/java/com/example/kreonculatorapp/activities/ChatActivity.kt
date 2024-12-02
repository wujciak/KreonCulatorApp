package com.example.kreonculatorapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.ChatRepository
import com.example.kreonculatorapp.firestore.Message
import com.example.kreonculatorapp.recyclerview.MessageAdapter
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private lateinit var adapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()
    private val chatRepository = ChatRepository()

    private val chatId: String by lazy { intent.getStringExtra("chatId") ?: "defaultChatId" }
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicjalizacja widoków
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Ustawienie adaptera dla RecyclerView
        adapter = MessageAdapter(messagesList, currentUserId)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter

        // Pobieranie wiadomości z Firestore
        chatRepository.getMessages(chatId) { messages ->
            messagesList.clear()
            messagesList.addAll(messages)
            adapter.notifyDataSetChanged()
            messagesRecyclerView.scrollToPosition(messagesList.size - 1) // Przewijanie na dół
        }

        // Wysłanie wiadomości
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = Message(
                    text = messageText,
                    senderId = currentUserId
                )

                chatRepository.sendMessage(chatId, message,
                    onSuccess = {
                        messageEditText.text.clear()
                    },
                    onFailure = { exception ->
                        exception.printStackTrace()
                    }
                )
            }
        }
    }
}
