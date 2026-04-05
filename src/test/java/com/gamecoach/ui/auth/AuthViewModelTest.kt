package com.gamecoach.ui.auth

import com.gamecoach.model.UserRole
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

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        com.gamecoach.data.MockRepository.resetForTesting()
        viewModel = AuthViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `etat initial est correct`() {
        val state = viewModel.authState.value
        assertFalse("isLoading devrait etre false", state.isLoading)
        assertFalse("isAuthenticated devrait etre false", state.isAuthenticated)
        assertNull("user devrait etre null", state.user)
        assertNull("error devrait etre null", state.error)
    }

    @Test
    fun `login reussit et met a jour user et isAuthenticated`() = runTest {
        // Préparer l'utilisateur dans le MockRepository
        val email = "test@test.com"
        val pass = "password"
        val user = com.gamecoach.model.User(id = "1", username = "Test", email = email, role = UserRole.PLAYER)
        com.gamecoach.data.MockRepository.registerUser(user, pass)
        com.gamecoach.data.MockRepository.approveUser(email) // Doit être Approved pour login
        
        viewModel.login(email, pass)
        
        // Fait oser la coroutine jusqu'a la fin du faux delai reseau
        advanceUntilIdle()
        
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertTrue("L'utilisateur devrait etre authentifie", state.isAuthenticated)
        assertNotNull("L'utilisateur ne devrait pas etre null", state.user)
        assertEquals(email, state.user?.email)
    }

    @Test
    fun `register reussit et enleve le flag de chargement sans authentifier`() = runTest {
        viewModel.register("MonUser", "mon@user.com", "pass", UserRole.PLAYER)
        
        advanceUntilIdle()
        
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        // L'implémentation actuelle de register ne connecte pas automatiquement l'user
        assertFalse(state.isAuthenticated)
        assertNull(state.user)
    }

    @Test
    fun `logout reinitialise correctement l'etat`() = runTest {
        // Préparer l'utilisateur
        val email = "test@test.com"
        val pass = "password"
        val user = com.gamecoach.model.User(id = "1", username = "Test", email = email, role = UserRole.PLAYER)
        com.gamecoach.data.MockRepository.registerUser(user, pass)
        com.gamecoach.data.MockRepository.approveUser(email)
        
        // Simule d'abord une connexion
        viewModel.login(email, pass)
        advanceUntilIdle()
        assertTrue(viewModel.authState.value.isAuthenticated)

        // Puis deconnexion
        viewModel.logout()
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertFalse(state.isAuthenticated)
        assertNull(state.user)
    }
}
