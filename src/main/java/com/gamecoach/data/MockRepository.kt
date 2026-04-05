package com.gamecoach.data

import androidx.compose.runtime.mutableStateListOf
import com.gamecoach.model.*
import java.util.UUID

object MockRepository {
    val registeredUsers = mutableStateListOf<User>(
        User(id = "admin_id", username = "Admin", email = "admin@amoa.inpt", role = UserRole.ADMIN, status = UserStatus.APPROVED, createdAt = "01/01/2024")
    )

    private val userPasswords = mutableMapOf(
        "admin@amoa.inpt" to "amoainpt"
    )

    val coaches = mutableStateListOf<Coach>()

    val sessions = mutableStateListOf<Session>()
    val messages = mutableStateListOf<Message>()

    fun registerUser(user: User, password: String, coachInfo: Coach? = null) {
        if (registeredUsers.none { it.email.equals(user.email, ignoreCase = true) }) {
            registeredUsers.add(user.copy(status = UserStatus.PENDING, createdAt = "Aujourd'hui"))
            userPasswords[user.email.lowercase().trim()] = password.trim()
            if (user.role == UserRole.COACH && coachInfo != null) {
                coaches.add(coachInfo.copy(status = CoachStatus.PENDING, id = user.id, email = user.email))
            }
        }
    }

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

    fun rejectUser(email: String) {
        val index = registeredUsers.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (index != -1) {
            val user = registeredUsers[index]
            registeredUsers[index] = user.copy(status = UserStatus.REJECTED)
            if (user.role == UserRole.COACH) {
                val coachIndex = coaches.indexOfFirst { it.email.equals(email, ignoreCase = true) }
                if (coachIndex != -1) {
                    coaches[coachIndex] = coaches[coachIndex].copy(status = CoachStatus.REJECTED)
                }
            }
        }
    }

    fun getPendingUsers(): List<User> = registeredUsers.filter { it.status == UserStatus.PENDING }
    fun isUserApproved(email: String): Boolean = registeredUsers.find { it.email.equals(email, ignoreCase = true) }?.status == UserStatus.APPROVED
    fun isUserRegistered(email: String): Boolean = registeredUsers.any { it.email.equals(email, ignoreCase = true) }
    
    fun verifyPassword(email: String, pass: String): Boolean {
        val storedPassword = userPasswords[email.lowercase().trim()]
        return storedPassword != null && storedPassword == pass.trim()
    }

    fun getUserByEmail(email: String): User? = registeredUsers.find { it.email.equals(email, ignoreCase = true) }
    fun getCoachById(id: String): Coach? = coaches.find { it.id == id }

    fun updateUsername(email: String, newUsername: String) {
        val userIndex = registeredUsers.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (userIndex != -1) {
            val oldName = registeredUsers[userIndex].username
            registeredUsers[userIndex] = registeredUsers[userIndex].copy(username = newUsername)
            for (i in sessions.indices) {
                val s = sessions[i]
                if (s.playerName == oldName) sessions[i] = s.copy(playerName = newUsername)
                if (s.coachName == oldName) sessions[i] = s.copy(coachName = newUsername)
            }
        }
    }

    fun addSession(session: Session) { sessions.add(0, session) }

    fun getSessionsForUser(email: String): List<Session> {
        val user = getUserByEmail(email) ?: return emptyList()
        return if (user.role == UserRole.COACH) {
            sessions.filter { it.coachName.equals(user.username, ignoreCase = true) }
        } else {
            sessions.filter { it.playerName.equals(user.username, ignoreCase = true) }
        }
    }

    fun getUserStats(email: String): Pair<Int, Double> {
        val user = getUserByEmail(email) ?: return Pair(0, 0.0)
        val userSessions = getSessionsForUser(email)
        return if (user.role == UserRole.COACH) {
            val completedCount = userSessions.count { it.status == SessionStatus.COMPLETED }
            val totalRevenue = userSessions.filter { it.isPaid }.sumOf { it.amount }
            Pair(completedCount, totalRevenue)
        } else {
            val completed = userSessions.filter { it.status == SessionStatus.COMPLETED }
            val totalHours = completed.sumOf { it.duration }.toDouble()
            Pair(completed.size, totalHours)
        }
    }

    fun getSessionById(id: String): Session? = sessions.find { it.id == id }

    fun updateSessionStatus(sessionId: String, status: SessionStatus) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) sessions[index] = sessions[index].copy(status = status)
    }

    fun validateSessionByCoach(sessionId: String) {
        updateSessionStatus(sessionId, SessionStatus.AWAITING_PAYMENT)
    }

    fun markSessionAsPaid(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            sessions[index] = sessions[index].copy(
                status = SessionStatus.ACCEPTED, 
                isPaid = true
            )
        }
    }

    fun markSessionAsFinishedByCoach(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            val session = sessions[index]
            val updated = session.copy(coachFinished = true)
            sessions[index] = updated
            checkIfSessionCompleted(index)
        }
    }

    fun markSessionAsFinishedByPlayer(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            val session = sessions[index]
            val updated = session.copy(playerFinished = true)
            sessions[index] = updated
            checkIfSessionCompleted(index)
        }
    }

    private fun checkIfSessionCompleted(index: Int) {
        val session = sessions[index]
        if (session.coachFinished && session.playerFinished) {
            sessions[index] = session.copy(status = SessionStatus.COMPLETED)
        }
    }

    fun sendMessage(message: Message) { messages.add(message) }
    fun getMessagesForSession(sessionId: String): List<Message> = messages.filter { it.sessionId == sessionId }
    
    fun updateCoachRating(coachName: String, newRating: Int) {
        val index = coaches.indexOfFirst { it.username.equals(coachName, ignoreCase = true) }
        if (index != -1) {
            val coach = coaches[index]
            val totalRating = coach.rating * coach.totalReviews
            val newTotalReviews = coach.totalReviews + 1
            val updatedRating = (totalRating + newRating) / newTotalReviews
            coaches[index] = coach.copy(
                rating = updatedRating,
                totalReviews = newTotalReviews
            )
        }
    }
}
