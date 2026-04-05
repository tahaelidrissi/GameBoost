package com.gamecoach.ui.navigation

sealed class Screen(val route: String) {
    // Common
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile?email={email}") {
        fun createRoute(email: String) = "profile?email=$email"
    }
    object EditProfile : Screen("edit_profile?email={email}") {
        fun createRoute(email: String) = "edit_profile?email=$email"
    }
    object Settings : Screen("settings")

    // Player
    object PlayerHome : Screen("player_home?email={email}") {
        fun createRoute(email: String) = "player_home?email=$email"
    }
    object CoachList : Screen("coach_list?email={email}") {
        fun createRoute(email: String) = "coach_list?email=$email"
    }
    object CoachDetail : Screen("coach_detail/{coachId}?email={email}") {
        fun createRoute(coachId: String, email: String) = "coach_detail/$coachId?email=$email"
    }
    object RequestSession : Screen("request_session/{coachId}?email={email}") {
        fun createRoute(coachId: String, email: String) = "request_session/$coachId?email=$email"
    }
    object PlayerSessions : Screen("player_sessions?email={email}") {
        fun createRoute(email: String) = "player_sessions?email=$email"
    }
    object SessionDetail : Screen("session_detail/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "session_detail/$sessionId?email=$email"
    }
    object Chat : Screen("chat/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "chat/$sessionId?email=$email"
    }
    object PlayerChatList : Screen("player_chat_list?email={email}") {
        fun createRoute(email: String) = "player_chat_list?email=$email"
    }
    object ReviewCoach : Screen("review_coach/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "review_coach/$sessionId?email=$email"
    }
    object Payment : Screen("payment/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "payment/$sessionId?email=$email"
    }

    // Coach
    object CoachHome : Screen("coach_home?email={email}") {
        fun createRoute(email: String) = "coach_home?email=$email"
    }
    object CoachApplication : Screen("coach_application")
    object CoachProfile : Screen("coach_profile?email={email}") {
        fun createRoute(email: String) = "coach_profile?email=$email"
    }
    object EditCoachProfile : Screen("edit_coach_profile")
    object CoachSessions : Screen("coach_sessions?email={email}") {
        fun createRoute(email: String) = "coach_sessions?email=$email"
    }
    object CoachSessionDetail : Screen("coach_session_detail/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "coach_session_detail/$sessionId?email=$email"
    }
    object CoachChat : Screen("coach_chat/{sessionId}?email={email}") {
        fun createRoute(sessionId: String, email: String) = "coach_chat/$sessionId?email=$email"
    }
    object CoachChatList : Screen("coach_chat_list?email={email}") {
        fun createRoute(email: String) = "coach_chat_list?email=$email"
    }
    object Revenue : Screen("revenue")
    object Reviews : Screen("reviews")

    // Admin
    object AdminHome : Screen("admin_home?email={email}") {
        fun createRoute(email: String) = "admin_home?email=$email"
    }
    object PendingCoaches : Screen("pending_coaches?email={email}") {
        fun createRoute(email: String) = "pending_coaches?email=$email"
    }
    object CoachDetailAdmin : Screen("coach_detail_admin/{coachId}") {
        fun createRoute(coachId: String) = "coach_detail_admin/$coachId"
    }
    object ManageUsers : Screen("manage_users")
}
