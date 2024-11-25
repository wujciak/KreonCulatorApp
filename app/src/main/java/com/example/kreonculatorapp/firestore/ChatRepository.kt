package com.example.kreonculatorapp.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun sendMessage(chatId: String, message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getMessages(chatId: String, onMessagesLoaded: (List<Message>) -> Unit) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                val messages = value?.toObjects(Message::class.java) ?: listOf()
                onMessagesLoaded(messages)
            }
    }
}
