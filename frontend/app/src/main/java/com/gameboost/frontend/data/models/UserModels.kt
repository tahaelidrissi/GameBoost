package com.gameboost.frontend.data.models

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val username: String,
    val role: Role,
    val isApproved: Boolean,
    val createdAt: LocalDateTime
) {
    enum class Role {
        JOUEUR, COACH, ADMIN
    }
}

data class AuthResponse(
    val token: String,
    val user: User
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val role: String
)

data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
)
