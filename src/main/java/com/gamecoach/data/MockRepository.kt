package com.gamecoach.data

import com.gamecoach.model.*

object MockRepository {
    // Liste des utilisateurs inscrits (simulation base de données)
    private val registeredUsers = mutableListOf(
        User(id = "1", username = "Joueur", email = "joueur@test.com", role = UserRole.PLAYER),
        User(id = "2", username = "Coach", email = "coach@test.com", role = UserRole.COACH),
        User(id = "3", username = "Admin", email = "admin@test.com", role = UserRole.ADMIN)
    )

    // Liste des sessions (simulation base de données)
    private val sessions = mutableListOf(
        Session(
            id = "1",
            coachName = "ProBoost",
            game = "Valorant",
            scheduledDate = "2024-03-10",
            scheduledTime = "16:00",
            duration = 1,
            amount = 35.0,
            status = SessionStatus.PENDING
        ),
        Session(
            id = "2",
            coachName = "LeagueExpert",
            game = "LoL",
            scheduledDate = "2024-03-08",
            scheduledTime = "14:00",
            duration = 2,
            amount = 80.0,
            status = SessionStatus.ACCEPTED,
            isPaid = false
        )
    )

    // --- Validation Inscription/Connexion ---
    
    fun registerUser(user: User) {
        if (registeredUsers.none { it.email.equals(user.email, ignoreCase = true) }) {
            registeredUsers.add(user)
        }
    }

    fun isUserRegistered(email: String): Boolean {
        return registeredUsers.any { it.email.equals(email, ignoreCase = true) }
    }

    // --- Validation Sessions/Paiements ---

    fun getAllSessions(): List<Session> = sessions

    fun getSessionById(id: String): Session? {
        return sessions.find { it.id == id }
    }

    fun canPaySession(sessionId: String): Boolean {
        val session = getSessionById(sessionId)
        // RÈGLE : Seule une session ACCEPTÉE et NON PAYÉE peut être payée
        return session != null && session.status == SessionStatus.ACCEPTED && !session.isPaid
    }

    fun markSessionAsPaid(sessionId: String) {
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index != -1 && sessions[index].status == SessionStatus.ACCEPTED) {
            sessions[index] = sessions[index].copy(isPaid = true)
        }
    }
}
