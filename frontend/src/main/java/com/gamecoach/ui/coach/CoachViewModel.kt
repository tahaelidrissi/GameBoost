package com.gamecoach.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CoachState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val error: String? = null
)

class CoachViewModel : ViewModel() {
    private val _state = MutableStateFlow(CoachState())
    val state: StateFlow<CoachState> = _state

    fun loadSessions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun acceptSession(sessionId: String) {
        viewModelScope.launch {
            // TODO: Appel API
        }
    }

    fun rejectSession(sessionId: String) {
        viewModelScope.launch {
            // TODO: Appel API
        }
    }
}