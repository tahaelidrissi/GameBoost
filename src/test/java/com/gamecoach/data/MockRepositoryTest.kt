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
        assertTrue(MockRepository.isUserRegistered("joueur@test.com"))
        assertTrue(MockRepository.isUserRegistered("coach@test.com"))
    }

    @Test
    fun `isUserRegistered est insensible à la casse`() {
        assertTrue(MockRepository.isUserRegistered("JOUEUR@TEST.COM"))
    }

    @Test
    fun `isUserRegistered retourne false pour un email inconnu`() {
        assertFalse(MockRepository.isUserRegistered("inconnu@test.com"))
    }

    @Test
    fun `registerUser ajoute un nouvel utilisateur avec succes`() {
        val newUser = User(id = "4", username = "Nouveau", email = "nouveau@test.com", role = UserRole.PLAYER)
        MockRepository.registerUser(newUser)
        
        assertTrue(MockRepository.isUserRegistered("nouveau@test.com"))
    }

    @Test
    fun `registerUser n ajoute pas de doublon d email`() {
        // L'email existe déjà, mème avec une casse différente
        val duplicateUser = User(id = "99", username = "Fake", email = "Joueur@TEST.com", role = UserRole.PLAYER)
        MockRepository.registerUser(duplicateUser)
        
        // On devrait toujours avoir l'original sans erreur
        assertTrue(MockRepository.isUserRegistered("joueur@test.com"))
    }

    // ─────────────────────────────────────────────
    // canPaySession
    // ─────────────────────────────────────────────

    @Test
    fun `canPaySession retourne true pour une session ACCEPTED non payée`() {
        // La session "2" est ACCEPTED et non payée
        assertTrue(MockRepository.canPaySession("2"))
    }

    @Test
    fun `canPaySession retourne false pour une session PENDING`() {
        // La session "1" est PENDING
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
    fun `markSessionAsPaid marque correctement la session comme payée`() {
        MockRepository.markSessionAsPaid("2")
        val session = MockRepository.getSessionById("2")
        
        assertNotNull(session)
        assertTrue(session!!.isPaid)
    }

    @Test
    fun `canPaySession retourne false just apres paiement`() {
        MockRepository.markSessionAsPaid("2")
        // Ne devrait plus être payable
        assertFalse(MockRepository.canPaySession("2"))
    }

    @Test
    fun `markSessionAsPaid ne modifie pas le statut isPaid d'une session non ACCEPTED`() {
        MockRepository.markSessionAsPaid("1") // PENDING
        val session = MockRepository.getSessionById("1")
        
        assertNotNull(session)
        assertFalse(session!!.isPaid)
    }

    // ─────────────────────────────────────────────
    // getSessionById / getAllSessions
    // ─────────────────────────────────────────────

    @Test
    fun `getSessionById retourne null pour un ID inexistant`() {
        assertNull(MockRepository.getSessionById("unknown-id"))
    }
    
    @Test
    fun `getAllSessions retourne l'ensemble des sessions`() {
        val sessions = MockRepository.getAllSessions()
        assertEquals(2, sessions.size)
    }
}
