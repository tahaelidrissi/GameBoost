package com.gamecoach.model

data class Review(
    val id: String = "",
    val sessionId: String = "",
    val coachId: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: String = ""
)