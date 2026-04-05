package com.gamecoach.data

import com.gamecoach.model.SessionStatus
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockRepositoryTest {

    @Before
    fun setUp() {
        // Remise à zéro des données avant chaque test pour éviter toute perturbation croisée
        MockRepository.resetForTesting()
    }

    // ─────────────────────────────────────────────
    // registerUser / isUserRegistered
    // ─────────────────────────────────────────────

    @Test
    fun `isUserRegistered retourne true pour un utilisateur existant`() {
        // Ajouter un utilisateur de test après le reset
        val user = User(id = "player1", username = "Joueur", email = "joueur@test.com", role = UserRole.PLAYER)
        MockRepository.registerUser(user, "password")
        
        assertTrue(MockRepository.isUserRegistered("joueur@test.com"))
    }

    @Test
    fun `isUserRegistered est insensible à la casse`() {
        val user = User(id = "player1", username = "Joueur", email = "joueur@test.com", role = UserRole.PLAYER)
        MockRepository.registerUser(user, "password")
        
        assertTrue(MockRepository.isUserRegistered("JOUEUR@TEST.COM"))
    }

    @Test
    fun `isUserRegistered retourne false pour un email inconnu`() {
        assertFalse(MockRepository.isUserRegistered("inconnu@test.com"))
    }

    @Test
    fun `registerUser ajoute un nouvel utilisateur avec succes`() {
        val newUser = User(id = "4", username = "Nouveau", email = "nouveau@test.com", role = UserRole.PLAYER)
        MockRepository.registerUser(newUser, "password123")
        
        assertTrue(MockRepository.isUserRegistered("nouveau@test.com"))
    }

    @Test
    fun `registerUser n ajoute pas de doublon d email`() {
        val user = User(id = "1", username = "Joueur", email = "joueur@test.com", role = UserRole.PLAYER)
        MockRepository.registerUser(user, "password")
        
        // L'email existe déjà, mème avec une casse différente
        val duplicateUser = User(id = "99", username = "Fake", email = "Joueur@TEST.com", role = UserRole.PLAYER)
        MockRepository.registerUser(duplicateUser, "wrong_pass")
        
        // On devrait toujours avoir l'original sans erreur
        assertTrue(MockRepository.isUserRegistered("joueur@test.com"))
        assertTrue(MockRepository.verifyPassword("joueur@test.com", "password"))
        assertFalse(MockRepository.verifyPassword("joueur@test.com", "wrong_pass"))
    }

    // ─────────────────────────────────────────────
    // canPaySession
    // ─────────────────────────────────────────────

    @Test
    fun `canPaySession retourne true pour une session AWAITING_PAYMENT non payée`() {
        val session = com.gamecoach.model.Session(
            id = "2", playerId = "p", playerName = "P", coachId = "c", coachName = "C", 
            game = "V", duration = 1, amount = 10.0, status = SessionStatus.AWAITING_PAYMENT, isPaid = false
        )
        MockRepository.addSession(session)
        assertTrue(MockRepository.canPaySession("2"))
    }

    @Test
    fun `canPaySession retourne false pour une session PENDING`() {
        val session = com.gamecoach.model.Session(
            id = "1", playerId = "p", playerName = "P", coachId = "c", coachName = "C", 
            game = "V", duration = 1, amount = 10.0, status = SessionStatus.PENDING, isPaid = false
        )
        MockRepository.addSession(session)
        assertFalse(MockRepository.canPaySession("1"))
    }

    @Test
    fun `canPaySession retourne false pour un ID inexistant`() {
        assertFalse(MockRepository.canPaySession("9999"))
    }

    // ─────────────────────────────────────────────
    // markSessionAsPaid
    // ─────────────────────────────────────────────

    @Test
    fun `markSessionAsPaid marque correctement la session comme payée et change le statut en ACCEPTED`() {
        val session = com.gamecoach.model.Session(
            id = "2", playerId = "p", playerName = "P", coachId = "c", coachName = "C", 
            game = "V", duration = 1, amount = 10.0, status = SessionStatus.AWAITING_PAYMENT, isPaid = false
        )
        MockRepository.addSession(session)
        
        MockRepository.markSessionAsPaid("2")
        val updatedSession = MockRepository.getSessionById("2")
        
        assertNotNull(updatedSession)
        assertTrue(updatedSession!!.isPaid)
        assertEquals(SessionStatus.ACCEPTED, updatedSession.status)
    }

    @Test
    fun `canPaySession retourne false just apres paiement`() {
        val session = com.gamecoach.model.Session(
            id = "2", playerId = "p", playerName = "P", coachId = "c", coachName = "C", 
            game = "V", duration = 1, amount = 10.0, status = SessionStatus.AWAITING_PAYMENT, isPaid = false
        )
        MockRepository.addSession(session)
        
        MockRepository.markSessionAsPaid("2")
        // Ne devrait plus être payable
        assertFalse(MockRepository.canPaySession("2"))
    }
    
    @Test
    fun `getAllSessions retourne l'ensemble des sessions`() {
        val s1 = com.gamecoach.model.Session(id="1", playerId="p", playerName="P", coachId="c", coachName="C", game="V", duration=1, amount=10.0)
        val s2 = com.gamecoach.model.Session(id="2", playerId="p", playerName="P", coachId="c", coachName="C", game="V", duration=1, amount=10.0)
        MockRepository.addSession(s1)
        MockRepository.addSession(s2)
        
        val sessions = MockRepository.getAllSessions()
        assertEquals(2, sessions.size)
    }
}
