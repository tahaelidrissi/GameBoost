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
            arguments = listOf(navArgument("email") { 
                type = NavType.StringType
                defaultValue = "joueur@test.com"
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "joueur@test.com"
            ProfileScreen(navController = navController, email = email)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // Player
        composable(
            route = Screen.PlayerHome.route,
            arguments = listOf(navArgument("email") { 
                type = NavType.StringType
                defaultValue = "joueur@test.com"
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "joueur@test.com"
            PlayerHomeScreen(navController = navController, email = email)
        }
        composable(Screen.CoachList.route) {
            CoachListScreen(navController = navController)
        }
        composable(
            route = Screen.CoachDetail.route,
            arguments = listOf(navArgument("coachId") { type = NavType.StringType })
        ) {
            CoachDetailScreen(navController = navController)
        }
        composable(
            route = Screen.RequestSession.route,
            arguments = listOf(navArgument("coachId") { type = NavType.StringType })
        ) {
            RequestSessionScreen(navController = navController)
        }
        composable(Screen.PlayerSessions.route) {
            PlayerSessionsScreen(navController = navController)
        }
        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            SessionDetailScreen(navController = navController)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            ChatScreen(navController = navController)
        }
        composable(Screen.PlayerChatList.route) {
            PlayerChatListScreen(navController = navController)
        }
        composable(
            route = Screen.ReviewCoach.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            ReviewCoachScreen(navController = navController)
        }
        composable(
            route = Screen.Payment.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            PaymentScreen(navController = navController)
        }

        // Coach
        composable(
            route = Screen.CoachHome.route,
            arguments = listOf(navArgument("email") { 
                type = NavType.StringType
                defaultValue = "coach@test.com"
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "coach@test.com"
            CoachHomeScreen(navController = navController, email = email)
        }
        composable(Screen.CoachApplication.route) {
            CoachApplicationScreen(navController = navController)
        }
        composable(
            route = Screen.CoachProfile.route,
            arguments = listOf(navArgument("email") { 
                type = NavType.StringType
                defaultValue = "coach@test.com"
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "coach@test.com"
            ProfileScreen(navController = navController, email = email)
        }
        composable(Screen.EditCoachProfile.route) {
            EditCoachProfileScreen(navController = navController)
        }
        composable(Screen.CoachSessions.route) {
            CoachSessionsScreen(navController = navController)
        }
        composable(
            route = Screen.CoachSessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            CoachSessionDetailScreen(navController = navController)
        }
        composable(
            route = Screen.CoachChat.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            CoachChatScreen(navController = navController)
        }
        composable(Screen.CoachChatList.route) {
            CoachChatListScreen(navController = navController)
        }
        composable(Screen.Revenue.route) {
            RevenueScreen(navController = navController)
        }
        composable(Screen.Reviews.route) {
            ReviewsScreen(navController = navController)
        }

        // Admin
        composable(
            route = Screen.AdminHome.route,
            arguments = listOf(navArgument("email") { 
                type = NavType.StringType
                defaultValue = "admin@test.com"
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "admin@test.com"
            AdminHomeScreen(navController = navController, email = email)
        }
        composable(Screen.PendingCoaches.route) {
            PendingCoachesScreen(navController = navController)
        }
        composable(
            route = Screen.CoachDetailAdmin.route,
            arguments = listOf(navArgument("coachId") { type = NavType.StringType })
        ) {
            CoachDetailAdminScreen(navController = navController)
        }
        composable(Screen.ManageUsers.route) {
            ManageUsersScreen(navController = navController)
        }
    }
}