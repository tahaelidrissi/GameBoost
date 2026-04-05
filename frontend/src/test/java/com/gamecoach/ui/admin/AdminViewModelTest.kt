package com.gamecoach.ui.admin

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
class AdminViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdminViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `etat initial est correct`() {
        val state = viewModel.state.value
        assertFalse("isLoading devrait etre false", state.isLoading)
        assertTrue("la liste pendingCoaches devrait etre vide", state.pendingCoaches.isEmpty())
        assertTrue("la liste pendingUsers devrait etre vide", state.pendingUsers.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `loadData termine sans erreur et reset isLoading`() = runTest {
        viewModel.loadData()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }
    
    @Test
    fun `approveUser termine sans crasher`() = runTest {
        viewModel.approveUser("test@email.com")
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `rejectUser termine sans crasher`() = runTest {
        viewModel.rejectUser("test@email.com")
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }
}
