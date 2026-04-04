package com.gamecoach.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.model.Coach
import com.gamecoach.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val isLoading: Boolean = false,
    val pendingCoaches: List<Coach> = emptyList(),
    val users: List<User> = emptyList(),
    val error: String? = null
)

class AdminViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state

    fun loadPendingCoaches() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun approveCoach(coachId: String) {
        viewModelScope.launch {
            // TODO: Appel API
        }
    }

    fun rejectCoach(coachId: String) {
        viewModelScope.launch {
            // TODO: Appel API
        }
    }
}