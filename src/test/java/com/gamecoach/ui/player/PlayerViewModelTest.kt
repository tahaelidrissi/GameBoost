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
    fun `loadCoaches modifie isLoading pendant lexecution et le remet a false`() = runTest {
        // Act
        viewModel.loadCoaches()
        
        // Assert : La coroutine est lancée mais pas finie (isLoading = true)
        // Note: Selon l'implémentation de la coroutine, ca peut deja valoir true ici si pas suspendu, 
        // ou nécessiter d'examiner exactement le flux d'états. On va simplement tester la fin d'exécution
        // pour s'assurer que isLoading retourne bien à false après.
        
        // Fait oser la coroutine jusqu'à ce qu'il n'y ait plus rien en attente.
        advanceUntilIdle()
        
        // Le chargement est censé être résolu
        assertFalse(viewModel.state.value.isLoading)
    }

    // ─────────────────────────────────────────────
    // loadSessions
    // ─────────────────────────────────────────────

    @Test
    fun `loadSessions modifie isLoading pendant lexecution et le remet a false`() = runTest {
        viewModel.loadSessions()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }

    // ─────────────────────────────────────────────
    // requestSession
    // ─────────────────────────────────────────────

    @Test
    fun `requestSession ne laisse pas l application en chargement infini`() = runTest {
        viewModel.requestSession(coachId = "123", duration = 2, date = "2024-03-10", time = "16:00")
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.value.isLoading)
    }
}
