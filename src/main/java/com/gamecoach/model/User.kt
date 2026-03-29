package com.gamecoach.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PLAYER,
    val createdAt: String = "",
    val profileImageUrl: String? = null
)

enum class UserRole {
    PLAYER,
    COACH,
    ADMIN
}