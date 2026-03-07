package com.gamecoach.util

object Constants {
    const val BASE_URL = "http://10.0.2.2:3000/api/"

    val GAMES = listOf(
        "Valorant",
        "League of Legends",
        "Counter-Strike 2",
        "Overwatch 2",
        "Dota 2",
        "Apex Legends",
        "Fortnite",
        "Rainbow Six Siege"
    )

    val VALORANT_RANKS = listOf(
        "Iron", "Bronze", "Silver", "Gold", "Platinum",
        "Diamond", "Ascendant", "Immortal", "Radiant"
    )

    val LOL_RANKS = listOf(
        "Iron", "Bronze", "Silver", "Gold", "Platinum",
        "Diamond", "Master", "Grandmaster", "Challenger"
    )

    val CS2_RANKS = listOf(
        "Silver", "Gold Nova", "Master Guardian",
        "Legendary Eagle", "Supreme", "Global Elite"
    )

    val OVERWATCH_RANKS = listOf(
        "Bronze", "Silver", "Gold", "Platinum",
        "Diamond", "Master", "Grandmaster", "Top 500"
    )

    val SESSION_DURATIONS = listOf(1, 2, 3, 4, 5)
}