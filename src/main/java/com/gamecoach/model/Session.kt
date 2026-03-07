package com.gamecoach.model

data class Session(
    val id: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val coachId: String = "",
    val coachName: String = "",
    val game: String = "",
    val duration: Int = 0,
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val amount: Double = 0.0,
    val status: SessionStatus = SessionStatus.PENDING,
    val createdAt: String = "",
    val isPaid: Boolean = false
)

enum class SessionStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}