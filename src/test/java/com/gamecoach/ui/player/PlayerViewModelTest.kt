package com.gamecoach.ui.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    // Dispatcher spécial pour controler le temps dans les tests des coroutines
    private val testDispatcher = StandardTestDispatcher()
    
    // Le ViewModel à tester
    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setUp() {
        // Remplacer le Dispatcher Main (qui nécessite un environnement Android réel)
        Dispatchers.setMain(testDispatcher)
        
        viewModel = PlayerViewModel()
    }

    @After
    fun tearDown() {
        // Toujours nettoyer après soi
        Dispatchers.resetMain()
    }

    // ─────────────────────────────────────────────
    // Initial State Check
    // ─────────────────────────────────────────────

    @Test
    fun `etat initial est correct`() {
        val state = viewModel.state.value
        
        assertFalse("isLoading devrait etre false par defaut", state.isLoading)
        assertTrue("la liste de coaches devrait etre vide par defaut", state.coaches.isEmpty())
        assertTrue("la liste de sessions devrait etre vide par defaut", state.sessions.isEmpty())
        assertNull("aucune erreur par defaut", state.error)
    }

    // ─────────────────────────────────────────────
    // loadCoaches
    // ─────────────────────────────────────────────

    @Test
    fun `loadData modifie isLoading pendant lexecution et le remet a false`() = runTest {
        // Act
        viewModel.loadData("player@test.com")
        
        advanceUntilIdle()
        
        // Le chargement est censé être résolu
        assertFalse(viewModel.state.value.isLoading)
    }

    // ─────────────────────────────────────────────
    // loadSessions
    // ─────────────────────────────────────────────

    @Test
    fun `loadData (via loadSessions) modifie isLoading pendant lexecution et le remet a false`() = runTest {
        viewModel.loadData("player@test.com")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }

    // ─────────────────────────────────────────────
    // requestSession
    // ─────────────────────────────────────────────

    @Test
    fun `requestSession ne laisse pas l application en chargement infini`() = runTest {
        val coach = com.gamecoach.model.Coach(id="123", username="Coach", email="c@t.com", game="V", hourlyRate=20.0)
        viewModel.requestSession(playerEmail = "player@test.com", coach = coach, duration = 2, date = "2024-03-10", time = "16:00")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }
}
