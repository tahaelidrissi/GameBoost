package com.gamecoach.data

import com.gamecoach.model.*
import java.util.UUID

object MockRepository {
    // Liste des utilisateurs inscrits (simulation base de données)
    private val registeredUsers = mutableListOf(
        User(id = "1", username = "Joueur", email = "joueur@test.com", role = UserRole.PLAYER, createdAt = "10 Janvier 2024"),
        User(id = "2", username = "Coach", email = "coach@test.com", role = UserRole.COACH, createdAt = "15 Février 2024"),
        User(id = "3", username = "Admin", email = "admin@test.com", role = UserRole.ADMIN, createdAt = "01 Janvier 2024")
    )

    // Liste des coachs disponibles
    private val coaches = mutableListOf(
        Coach(id = "1", username = "ProBoost", game = "Valorant", rank = "Radiant", hourlyRate = 35.0, rating = 4.8, totalReviews = 42, status = CoachStatus.APPROVED),
        Coach(id = "2", username = "LeagueExpert", game = "League of Legends", rank = "Challenger", hourlyRate = 40.0, rating = 4.9, totalReviews = 87, status = CoachStatus.APPROVED),
        Coach(id = "3", username = "CS2Expert", game = "Counter-Strike 2", rank = "Global Elite", hourlyRate = 30.0, rating = 4.7, totalReviews = 35, status = CoachStatus.APPROVED),
        Coach(id = "4", username = "OverwatchPro", game = "Overwatch 2", rank = "Grandmaster", hourlyRate = 25.0, rating = 4.6, totalReviews = 28, status = CoachStatus.APPROVED)
    )

    // Liste des sessions (simulation base de données)
    private val sessions = mutableListOf(
        Session(
            id = "1",
            playerId = "1",
            playerName = "Joueur",
            coachName = "ProBoost",
            game = "Valorant",
            scheduledDate = "10/03/2024",
            scheduledTime = "16:00",
            duration = 1,
            amount = 35.0,
            status = SessionStatus.PENDING
        ),
        Session(
            id = "2",
            playerId = "1",
            playerName = "Joueur",
            coachName = "LeagueExpert",
            game = "LoL",
            scheduledDate = "08/03/2024",
            scheduledTime = "14:00",
            duration = 2,
            amount = 80.0,
            status = SessionStatus.ACCEPTED,
            isPaid = false
        ),
        Session(
            id = "3",
            playerId = "1",
            playerName = "Joueur",
            coachName = "CS2Expert",
            game = "Counter-Strike 2",
            scheduledDate = "28/02/2024",
            scheduledTime = "16:00",
            duration = 2,
            amount = 70.0,
            status = SessionStatus.COMPLETED,
            isPaid = true
        )
    )

    // --- Gestion des Coachs ---
    fun getCoachById(id: String): Coach? = coaches.find { it.id == id }
    fun getAllCoaches(): List<Coach> = coaches

    // --- Validation Inscription/Connexion ---
    
    fun registerUser(user: User) {
        if (registeredUsers.none { it.email.equals(user.email, ignoreCase = true) }) {
            registeredUsers.add(user.copy(createdAt = "Aujourd'hui"))
        }
    }

    fun isUserRegistered(email: String): Boolean {
        return registeredUsers.any { it.email.equals(email, ignoreCase = true) }
    }

    fun getUserByEmail(email: String): User? {
        return registeredUsers.find { it.email.equals(email, ignoreCase = true) }
    }

    fun updateUsername(email: String, newUsername: String) {
        val index = registeredUsers.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (index != -1) {
            val oldName = registeredUsers[index].username
            registeredUsers[index] = registeredUsers[index].copy(username = newUsername)
            
            // Mettre à jour le nom dans les sessions existantes pour la cohérence
            sessions.forEachIndexed { i, s ->
                if (s.playerName == oldName) sessions[i] = s.copy(playerName = newUsername)
                if (s.coachName == oldName) sessions[i] = s.copy(coachName = newUsername)
            }
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

    fun getUserStats(email: String): Pair<Int, Int> {
        val userSessions = getSessionsForUser(email)
        val count = userSessions.size
        val hours = userSessions.sumOf { it.duration }
        return Pair(count, hours)
    }

    fun getSessionById(id: String): Session? = sessions.find { it.id == id }

    fun canPaySession(sessionId: String): Boolean {
        val session = getSessionById(sessionId)
        return session != null && session.status == SessionStatus.ACCEPTED && !session.isPaid
    }

    fun markSessionAsPaid(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1 && sessions[index].status == SessionStatus.ACCEPTED) {
            sessions[index] = sessions[index].copy(isPaid = true)
        }
    }
}
