package com.gamecoach.ui.navigation

sealed class Screen(val route: String) {
    // Common
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")

    // Player
    object PlayerHome : Screen("player_home")
    object CoachList : Screen("coach_list")
    object CoachDetail : Screen("coach_detail/{coachId}") {
        fun createRoute(coachId: String) = "coach_detail/$coachId"
    }
    object RequestSession : Screen("request_session/{coachId}") {
        fun createRoute(coachId: String) = "request_session/$coachId"
    }
    object PlayerSessions : Screen("player_sessions")
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "session_detail/$sessionId"
    }
    object Chat : Screen("chat/{sessionId}") {
        fun createRoute(sessionId: String) = "chat/$sessionId"
    }
    object PlayerChatList : Screen("player_chat_list")
    object ReviewCoach : Screen("review_coach/{sessionId}") {
        fun createRoute(sessionId: String) = "review_coach/$sessionId"
    }
    object Payment : Screen("payment/{sessionId}") {
        fun createRoute(sessionId: String) = "payment/$sessionId"
    }

    // Coach
    object CoachHome : Screen("coach_home")
    object CoachApplication : Screen("coach_application")
    object CoachProfile : Screen("coach_profile")
    object EditCoachProfile : Screen("edit_coach_profile")
    object CoachSessions : Screen("coach_sessions")
    object CoachSessionDetail : Screen("coach_session_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "coach_session_detail/$sessionId"
    }
    object CoachChat : Screen("coach_chat/{sessionId}") {
        fun createRoute(sessionId: String) = "coach_chat/$sessionId"
    }
    object CoachChatList : Screen("coach_chat_list")
    object Revenue : Screen("revenue")
    object Reviews : Screen("reviews")

    // Admin
    object AdminHome : Screen("admin_home")
    object PendingCoaches : Screen("pending_coaches")
    object CoachDetailAdmin : Screen("coach_detail_admin/{coachId}") {
        fun createRoute(coachId: String) = "coach_detail_admin/$coachId"
    }
    object ManageUsers : Screen("manage_users")
}