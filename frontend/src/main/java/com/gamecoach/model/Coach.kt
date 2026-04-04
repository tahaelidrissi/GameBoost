package com.gamecoach.model

data class Coach(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val game: String = "",
    val rank: String = "",
    val bio: String = "",
    val hourlyRate: Double = 0.0,
    val proofImageBase64: String? = null,
    val status: CoachStatus = CoachStatus.PENDING,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalSessions: Int = 0,
    val totalEarnings: Double = 0.0,
    val createdAt: String = ""
)

enum class CoachStatus {
    PENDING,
    APPROVED,
    REJECTED
}
