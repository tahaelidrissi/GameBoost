package com.gamecoach.ui.profile

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
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `etat initial est correct`() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.user)
        assertNull(state.error)
    }

    @Test
    fun `loadProfile execute et enleve le flag de chargement`() = runTest {
        viewModel.loadProfile()
        
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `updateProfile execute et enleve le flag de chargement`() = runTest {
        viewModel.updateProfile("NouveauPseudonyme")
        
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }
}
