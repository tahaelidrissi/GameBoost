package com.gamecoach.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PLAYER,
    val status: UserStatus = UserStatus.PENDING, // Nouvel état pour la validation admin
    val createdAt: String = "",
    val profileImageUrl: String? = null
)

enum class UserRole {
    PLAYER,
    COACH,
    ADMIN
}

enum class UserStatus {
    PENDING,
    APPROVED,
    REJECTED
}
