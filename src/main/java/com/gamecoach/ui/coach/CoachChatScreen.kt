package com.gamecoach.ui.coach

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.gamecoach.ui.player.ChatScreen

@Composable
fun CoachChatScreen(navController: NavController, sessionId: String, email: String) {
    // Réutilisation du ChatScreen avec les paramètres de session et d'utilisateur
    ChatScreen(
        navController = navController,
        sessionId = sessionId,
        currentUserEmail = email
    )
}
