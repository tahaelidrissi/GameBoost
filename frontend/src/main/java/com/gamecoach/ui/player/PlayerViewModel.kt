package com.gamecoach.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.model.Coach
import com.gamecoach.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlayerState(
    val isLoading: Boolean = false,
    val coaches: List<Coach> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val error: String? = null
)

class PlayerViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    fun loadCoaches() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun loadSessions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun requestSession(coachId: String, duration: Int, date: String, time: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}