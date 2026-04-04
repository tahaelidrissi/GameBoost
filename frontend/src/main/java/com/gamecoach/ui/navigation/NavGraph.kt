package com.gamecoach.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gamecoach.ui.admin.*
import com.gamecoach.ui.auth.*
import com.gamecoach.ui.coach.*
import com.gamecoach.ui.player.*
import com.gamecoach.ui.profile.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Profile
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ProfileScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EditProfileScreen(navController = navController, email = email)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // Player
        composable(
            route = Screen.PlayerHome.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PlayerHomeScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.CoachList.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachListScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.CoachDetail.route,
            arguments = listOf(
                navArgument("coachId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val coachId = backStackEntry.arguments?.getString("coachId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachDetailScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.RequestSession.route,
            arguments = listOf(
                navArgument("coachId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val coachId = backStackEntry.arguments?.getString("coachId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RequestSessionScreen(navController = navController, coachId = coachId, email = email)
        }
        composable(
            route = Screen.PlayerSessions.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PlayerSessionsScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SessionDetailScreen(navController = navController, sessionId = sessionId, email = email)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ChatScreen(navController = navController, sessionId = sessionId, currentUserEmail = email)
        }
        composable(Screen.PlayerChatList.route) {
            PlayerChatListScreen(navController = navController)
        }
        composable(
            route = Screen.ReviewCoach.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ReviewCoachScreen(navController = navController, sessionId = sessionId)
        }
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PaymentScreen(navController = navController)
        }

        // Coach
        composable(
            route = Screen.CoachHome.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachHomeScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.CoachSessions.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachSessionsScreen(navController = navController, email = email)
        }
        composable(
            route = Screen.CoachSessionDetail.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachSessionDetailScreen(navController = navController, sessionId = sessionId, email = email)
        }
        composable(
            route = Screen.CoachChat.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachChatScreen(navController = navController, sessionId = sessionId, email = email)
        }
        composable(Screen.CoachChatList.route) {
            CoachChatListScreen(navController = navController)
        }
        composable(Screen.Revenue.route) {
            RevenueScreen(navController = navController)
        }

        // Admin
        composable(
            route = Screen.AdminHome.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            AdminHomeScreen(navController = navController, email = email)
        }
        composable(Screen.PendingCoaches.route) {
            PendingCoachesScreen(navController = navController)
        }
        composable(
            route = Screen.CoachDetailAdmin.route,
            arguments = listOf(
                navArgument("coachId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val coachId = backStackEntry.arguments?.getString("coachId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CoachDetailAdminScreen(navController = navController, coachId = coachId)
        }
    }
}
