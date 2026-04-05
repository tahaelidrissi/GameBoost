package com.gamecoach.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Coach
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class PlayerState(
    val isLoading: Boolean = false,
    val coaches: List<Coach> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val error: String? = null,
    val totalSessions: Int = 0,
    val totalHours: Double = 0.0
)

class PlayerViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    fun loadData(playerEmail: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulation
            kotlinx.coroutines.delay(500)
            
            val coaches = MockRepository.coaches.filter { it.status == com.gamecoach.model.CoachStatus.APPROVED }
            val sessions = MockRepository.getSessionsForUser(playerEmail)
            val stats = MockRepository.getUserStats(playerEmail)
            
            _state.value = _state.value.copy(
                isLoading = false,
                coaches = coaches,
                sessions = sessions,
                totalSessions = stats.first,
                totalHours = stats.second
            )
        }
    }

    fun requestSession(playerEmail: String, coach: Coach, duration: Int, date: String, time: String) {
        viewModelScope.launch {
            val player = MockRepository.getUserByEmail(playerEmail) ?: return@launch
            
            val newSession = Session(
                id = UUID.randomUUID().toString(),
                playerId = player.id,
                playerName = player.username,
                coachId = coach.id,
                coachName = coach.username,
                game = coach.game,
                duration = duration,
                scheduledDate = date,
                scheduledTime = time,
                amount = coach.hourlyRate * duration,
                status = SessionStatus.PENDING
            )
            
            MockRepository.addSession(newSession)
            loadData(playerEmail)
        }
    }

    fun paySession(playerEmail: String, sessionId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            kotlinx.coroutines.delay(1000) // Simulation paiement
            
            MockRepository.markSessionAsPaid(sessionId)
            loadData(playerEmail)
        }
    }
}
