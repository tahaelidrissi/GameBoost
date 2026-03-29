package com.gamecoach.model

data class Message(
    val id: String = "",
    val sessionId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false
)