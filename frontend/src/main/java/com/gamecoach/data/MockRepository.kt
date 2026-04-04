package com.gamecoach.data

import com.gamecoach.model.*
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MockRepository {
    // Liste des utilisateurs inscrits (simulation base de données)
    private val registeredUsers = mutableListOf(
        User(id = "3", username = "Admin", email = "admin@amoa.inpt", role = UserRole.ADMIN, status = UserStatus.APPROVED, createdAt = "01/01/2024")
    )

    // Mappage des mots de passe (pour la simulation)
    private val userPasswords = mutableMapOf(
        "admin@amoa.inpt" to "amoainpt"
    )

    // Liste des coachs détaillés
    private val coaches = mutableListOf(
        Coach(id = "1", username = "ProBoost", game = "Valorant", rank = "Radiant", hourlyRate = 35.0, rating = 4.8, totalReviews = 42, status = CoachStatus.APPROVED),
        Coach(id = "2", username = "LeagueExpert", game = "League of Legends", rank = "Challenger", hourlyRate = 40.0, rating = 4.9, totalReviews = 87, status = CoachStatus.APPROVED)
    )

    // Liste des sessions
    private val sessions = mutableListOf<Session>()

    // Liste globale des messages (simulation de base de données de chat)
    private val messages = mutableListOf<Message>()

    // --- Gestion des Utilisateurs (Cycle Admin) ---
    
    fun registerUser(user: User, coachInfo: Coach? = null) {
        if (registeredUsers.none { it.email.equals(user.email, ignoreCase = true) }) {
            registeredUsers.add(user.copy(status = UserStatus.PENDING, createdAt = "Aujourd'hui"))
            if (user.role == UserRole.COACH && coachInfo != null) {
                coaches.add(coachInfo.copy(status = CoachStatus.PENDING, id = user.id))
            }
        }
    }

    fun getPendingUsers(): List<User> = registeredUsers.filter { it.status == UserStatus.PENDING }

    fun approveUser(email: String) {
        val index = registeredUsers.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (index != -1) {
            val user = registeredUsers[index]
            registeredUsers[index] = user.copy(status = UserStatus.APPROVED)
            if (user.role == UserRole.COACH) {
                val coachIndex = coaches.indexOfFirst { it.email.equals(email, ignoreCase = true) }
                if (coachIndex != -1) {
                    coaches[coachIndex] = coaches[coachIndex].copy(status = CoachStatus.APPROVED)
                }
            }
        }
    }

    fun isUserApproved(email: String): Boolean {
        return registeredUsers.find { it.email.equals(email, ignoreCase = true) }?.status == UserStatus.APPROVED
    }

    fun isUserRegistered(email: String): Boolean = registeredUsers.any { it.email.equals(email, ignoreCase = true) }
    
    fun verifyPassword(email: String, pass: String): Boolean {
        if (email.equals("admin@amoa.inpt", ignoreCase = true)) {
            return userPasswords[email] == pass
        }
        return true 
    }

    fun getUserByEmail(email: String): User? = registeredUsers.find { it.email.equals(email, ignoreCase = true) }

    fun updateUsername(email: String, newUsername: String) {
        val index = registeredUsers.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (index != -1) {
            val oldName = registeredUsers[index].username
            registeredUsers[index] = registeredUsers[index].copy(username = newUsername)
            sessions.forEachIndexed { i, s ->
                if (s.playerName == oldName) sessions[i] = s.copy(playerName = newUsername)
                if (s.coachName == oldName) sessions[i] = s.copy(coachName = newUsername)
            }
        }
    }

    // --- Gestion des Coachs ---
    fun getCoachById(id: String): Coach? = coaches.find { it.id == id }
    fun getPendingCoaches(): List<Coach> = coaches.filter { it.status == CoachStatus.PENDING }
    fun getAllCoachesForPlayers(): List<Coach> = coaches.filter { it.status == CoachStatus.APPROVED }

    fun updateCoachRating(coachName: String, newRating: Int) {
        val coach = coaches.find { it.username.equals(coachName, ignoreCase = true) }
        if (coach != null) {
            val totalPoints = coach.rating * coach.totalReviews
            val newTotalReviews = coach.totalReviews + 1
            val updatedRating = (totalPoints + newRating) / newTotalReviews
            val index = coaches.indexOf(coach)
            coaches[index] = coach.copy(rating = updatedRating, totalReviews = newTotalReviews)
        }
    }

    // --- Gestion des Sessions ---

    fun addSession(session: Session) {
        sessions.add(0, session)
    }

    fun getSessionsForUser(email: String): List<Session> {
        val user = getUserByEmail(email) ?: return emptyList()
        return if (user.role == UserRole.COACH) {
            sessions.filter { it.coachName.equals(user.username, ignoreCase = true) }
        } else {
            sessions.filter { it.playerName.equals(user.username, ignoreCase = true) }
        }
    }

    fun updateSessionStatus(sessionId: String, status: SessionStatus) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            sessions[index] = sessions[index].copy(status = status)
        }
    }

    fun validateSessionByCoach(sessionId: String) {
        updateSessionStatus(sessionId, SessionStatus.AWAITING_PAYMENT)
    }

    fun confirmPayment(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1 && sessions[index].status == SessionStatus.AWAITING_PAYMENT) {
            sessions[index] = sessions[index].copy(status = SessionStatus.ACCEPTED, isPaid = true)
        }
    }

    fun completeSession(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1 && sessions[index].status == SessionStatus.ACCEPTED) {
            sessions[index] = sessions[index].copy(status = SessionStatus.COMPLETED)
        }
    }

    fun getSessionById(id: String): Session? = sessions.find { it.id == id }

    fun getUserStats(email: String): Pair<Int, Double> {
        val user = getUserByEmail(email) ?: return Pair(0, 0.0)
        val userSessions = getSessionsForUser(email)
        return if (user.role == UserRole.COACH) {
            val completed = userSessions.filter { it.status == SessionStatus.COMPLETED }
            Pair(completed.size, completed.sumOf { it.amount })
        } else {
            val completed = userSessions.filter { it.status == SessionStatus.COMPLETED }
            Pair(completed.size, completed.sumOf { it.duration }.toDouble())
        }
    }

    // --- Gestion du Chat ---

    fun sendMessage(message: Message) {
        messages.add(message)
    }

    fun getMessagesForSession(sessionId: String): List<Message> {
        return messages.filter { it.sessionId == sessionId }
    }
}
