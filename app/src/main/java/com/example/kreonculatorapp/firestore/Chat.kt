package com.example.kreonculatorapp.firestore

data class Chat(
    val chatId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)
