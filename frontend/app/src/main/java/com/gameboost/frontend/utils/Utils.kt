package com.gameboost.frontend.utils

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun formatDate(dateTime: LocalDateTime): String {
        return dateTime.format(dateFormatter)
    }

    fun formatTime(dateTime: LocalDateTime): String {
        return dateTime.format(timeFormatter)
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }
}

object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 && 
               password.any { it.isUpperCase() } && 
               password.any { it.isDigit() }
    }

    fun isUsernameValid(username: String): Boolean {
        return username.length in 3..20 && username.matches(Regex("[a-zA-Z0-9_]+"))
    }
}

object Constants {
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_USERNAME_LENGTH = 20
    const val MIN_USERNAME_LENGTH = 3
    
    // Jeux supportés
    val SUPPORTED_GAMES = listOf("Valorant", "League of Legends", "Counter-Strike 2", "Dota 2")
    
    // Rangs par jeu
    val VALORANT_RANKS = listOf("Iron", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Immortal", "Radiant")
    val LOL_RANKS = listOf("Iron", "Bronze", "Silver", "Gold", "Platinum", "Emerald", "Diamond", "Master", "Grandmaster", "Challenger")
}
