package com.gameboost.frontend.data.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class CoachProfile(
    val id: Long,
    val userId: String,
    val gameTitle: String,
    val rank: String,
    val bio: String,
    val hourlyRate: BigDecimal,
    val proofImage: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CoachResponse(
    val id: Long,
    val username: String,
    val gameTitle: String,
    val rank: String,
    val bio: String,
    val hourlyRate: BigDecimal,
    val averageRating: Double,
    val totalReviews: Int,
    val reviews: List<ReviewResponse>? = null
)

data class CoachProfileRequest(
    val gameTitle: String,
    val rank: String,
    val bio: String,
    val hourlyRate: BigDecimal,
    val proofImage: String? = null
)
