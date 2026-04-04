package com.gamecoach.ui.coach

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
class CoachViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CoachViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CoachViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `etat initial est correct`() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.sessions.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `loadSessions met isLoading a jour`() = runTest {
        viewModel.loadSessions()
        
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `acceptSession execute sans crasher l etat`() = runTest {
        viewModel.acceptSession("session-id-1")
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `rejectSession execute sans crasher l etat`() = runTest {
        viewModel.rejectSession("session-id-1")
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }
}
