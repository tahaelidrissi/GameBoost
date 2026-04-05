package com.gameboost.frontend.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameboost.frontend.data.models.*
import com.gameboost.frontend.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _sessionState = MutableLiveData<SessionState>(SessionState.Idle)
    val sessionState: LiveData<SessionState> = _sessionState

    private val _sessions = MutableLiveData<List<SessionResponse>>()
    val sessions: LiveData<List<SessionResponse>> = _sessions

    private val _selectedSession = MutableLiveData<Session?>()
    val selectedSession: LiveData<Session?> = _selectedSession

    fun createSession(coachId: Long, durationHours: Int, scheduledAt: LocalDateTime) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val request = SessionRequest(coachId, durationHours, scheduledAt)
                val result = sessionRepository.createSession(request)
                result.onSuccess { session ->
                    _sessionState.value = SessionState.Success("Session créée avec succès")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors de la création")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun getSessions(status: String? = null) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val result = sessionRepository.getSessions(status)
                result.onSuccess { sessionList ->
                    _sessions.value = sessionList
                    _sessionState.value = SessionState.Success("Sessions chargées")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors du chargement")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun acceptSession(sessionId: Long) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val result = sessionRepository.acceptSession(sessionId)
                result.onSuccess {
                    _sessionState.value = SessionState.Success("Session acceptée")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors de l'acceptation")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun completeSession(sessionId: Long) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val result = sessionRepository.completeSession(sessionId)
                result.onSuccess {
                    _sessionState.value = SessionState.Success("Session marquée comme terminée")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors de la finalisation")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun confirmSession(sessionId: Long) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val result = sessionRepository.confirmSession(sessionId)
                result.onSuccess {
                    _sessionState.value = SessionState.Success("Session confirmée")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors de la confirmation")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun paySession(sessionId: Long) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val result = sessionRepository.paySession(sessionId)
                result.onSuccess {
                    _sessionState.value = SessionState.Success("Paiement simulé avec succès")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors du paiement")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun reviewSession(sessionId: Long, rating: Int, comment: String?) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val request = ReviewRequest(rating, comment)
                val result = sessionRepository.reviewSession(sessionId, request)
                result.onSuccess {
                    _sessionState.value = SessionState.Success("Avis enregistré")
                }.onFailure { exception ->
                    _sessionState.value = SessionState.Error(exception.message ?: "Erreur lors de l'enregistrement")
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    sealed class SessionState {
        object Idle : SessionState()
        object Loading : SessionState()
        data class Success(val message: String) : SessionState()
        data class Error(val message: String) : SessionState()
    }
}
