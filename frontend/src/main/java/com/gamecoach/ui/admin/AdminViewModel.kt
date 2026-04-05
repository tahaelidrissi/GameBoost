package com.gamecoach.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Coach
import com.gamecoach.model.User
import com.gamecoach.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val isLoading: Boolean = false,
    val pendingCoaches: List<Coach> = emptyList(),
    val pendingUsers: List<User> = emptyList(),
    val error: String? = null
)

class AdminViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulation
            kotlinx.coroutines.delay(500)
            
            val pending = MockRepository.getPendingUsers()
            val pendingCoaches = MockRepository.coaches.filter { it.status == com.gamecoach.model.CoachStatus.PENDING }
            
            _state.value = _state.value.copy(
                isLoading = false,
                pendingUsers = pending,
                pendingCoaches = pendingCoaches
            )
        }
    }

    fun approveUser(email: String) {
        viewModelScope.launch {
            MockRepository.approveUser(email)
            loadData() // Rafraîchir
        }
    }

    fun rejectUser(email: String) {
        viewModelScope.launch {
            MockRepository.rejectUser(email)
            loadData() // Rafraîchir
        }
    }
}
