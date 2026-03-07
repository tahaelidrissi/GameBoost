package com.gamecoach.ui.coach

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.gamecoach.ui.player.ChatScreen

@Composable
fun CoachChatScreen(navController: NavController) {
    // Réutilisation du ChatScreen du joueur
    ChatScreen(navController = navController)
}