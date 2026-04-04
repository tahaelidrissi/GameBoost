package com.gamecoach.util

import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatCurrency(amount: Double): String {
        return String.format("%.2f€", amount)
    }

    fun getRanksByGame(game: String): List<String> {
        return when (game) {
            "Valorant" -> Constants.VALORANT_RANKS
            "League of Legends" -> Constants.LOL_RANKS
            "Counter-Strike 2" -> Constants.CS2_RANKS
            "Overwatch 2" -> Constants.OVERWATCH_RANKS
            else -> emptyList()
        }
    }

    fun getCurrentDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

    fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(Date())
    }
}