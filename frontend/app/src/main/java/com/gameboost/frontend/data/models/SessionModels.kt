package com.gameboost.frontend.data.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Session(
    val id: Long,
    val playerId: String,
    val coachId: Long,
    val status: Status,
    val durationHours: Int,
    val amount: BigDecimal,
    val scheduledAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    enum class Status {
        REQUESTED, ACCEPTED, COMPLETED, CONFIRMED, PAID
    }
}

data class SessionResponse(
    val id: Long,
    val coach: CoachInfo? = null,
    val player: PlayerInfo? = null,
    val status: String,
    val durationHours: Int,
    val amount: BigDecimal,
    val scheduledAt: LocalDateTime
)

data class CoachInfo(
    val id: Long,
    val username: String,
    val gameTitle: String
)

data class PlayerInfo(
    val id: String,
    val username: String
)

data class SessionRequest(
    val coachId: Long,
    val durationHours: Int,
    val scheduledAt: LocalDateTime
)

data class Review(
    val id: Long,
    val sessionId: Long,
    val rating: Int,
    val comment: String? = null,
    val createdAt: LocalDateTime
)

data class ReviewRequest(
    val rating: Int,
    val comment: String? = null
)

data class ReviewResponse(
    val rating: Int,
    val comment: String?,
    val createdAt: LocalDateTime,
    val author: String? = null
)

data class Message(
    val id: Long,
    val sessionId: Long,
    val senderId: String,
    val content: String,
    val createdAt: LocalDateTime
)

data class MessageRequest(
    val content: String
)

data class MessageResponse(
    val id: Long,
    val sessionId: Long,
    val senderId: String,
    val senderName: String,
    val content: String,
    val createdAt: LocalDateTime
)
