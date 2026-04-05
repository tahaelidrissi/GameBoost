package com.gamecoach.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CoachState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val totalRevenue: Double = 0.0,
    val totalSessions: Int = 0,
    val error: String? = null
)

class CoachViewModel : ViewModel() {
    private val _state = MutableStateFlow(CoachState())
    val state: StateFlow<CoachState> = _state

    fun loadData(coachEmail: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulation
            kotlinx.coroutines.delay(500)
            
            val sessions = MockRepository.getSessionsForUser(coachEmail)
            val stats = MockRepository.getUserStats(coachEmail)
            
            _state.value = _state.value.copy(
                isLoading = false,
                sessions = sessions,
                totalSessions = stats.first,
                totalRevenue = stats.second
            )
        }
    }

    fun acceptSession(coachEmail: String, sessionId: String) {
        viewModelScope.launch {
            MockRepository.validateSessionByCoach(sessionId)
            loadData(coachEmail)
        }
    }

    fun rejectSession(coachEmail: String, sessionId: String) {
        viewModelScope.launch {
            MockRepository.updateSessionStatus(sessionId, SessionStatus.REJECTED)
            loadData(coachEmail)
        }
    }

    fun completeSession(coachEmail: String, sessionId: String) {
        viewModelScope.launch {
            // Mise à jour pour utiliser la nouvelle logique de fin de séance par les deux parties
            MockRepository.markSessionAsFinishedByCoach(sessionId)
            loadData(coachEmail)
        }
    }
}
