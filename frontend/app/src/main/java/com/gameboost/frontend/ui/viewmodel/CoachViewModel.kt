package com.gameboost.frontend.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameboost.frontend.data.models.*
import com.gameboost.frontend.data.repository.CoachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val coachRepository: CoachRepository
) : ViewModel() {

    private val _coachState = MutableLiveData<CoachState>(CoachState.Idle)
    val coachState: LiveData<CoachState> = _coachState

    private val _coaches = MutableLiveData<List<CoachResponse>>()
    val coaches: LiveData<List<CoachResponse>> = _coaches

    private val _selectedCoach = MutableLiveData<CoachResponse?>()
    val selectedCoach: LiveData<CoachResponse?> = _selectedCoach

    private val _earnings = MutableLiveData<Double>(0.0)
    val earnings: LiveData<Double> = _earnings

    fun fetchCoaches(game: String? = null, rank: String? = null) {
        viewModelScope.launch {
            _coachState.value = CoachState.Loading
            try {
                val result = coachRepository.getCoaches(game, rank)
                result.onSuccess { coachList ->
                    _coaches.value = coachList
                    _coachState.value = CoachState.Success("Coachs chargés")
                }.onFailure { exception ->
                    _coachState.value = CoachState.Error(exception.message ?: "Erreur lors du chargement des coachs")
                }
            } catch (e: Exception) {
                _coachState.value = CoachState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun getCoachById(coachId: Long) {
        viewModelScope.launch {
            _coachState.value = CoachState.Loading
            try {
                val result = coachRepository.getCoachById(coachId)
                result.onSuccess { coach ->
                    _selectedCoach.value = coach
                    _coachState.value = CoachState.Success("Coach chargé")
                }.onFailure { exception ->
                    _coachState.value = CoachState.Error(exception.message ?: "Erreur lors du chargement du coach")
                }
            } catch (e: Exception) {
                _coachState.value = CoachState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun createCoachProfile(
        gameTitle: String,
        rank: String,
        bio: String,
        hourlyRate: BigDecimal,
        proofImage: String?
    ) {
        viewModelScope.launch {
            _coachState.value = CoachState.Loading
            try {
                val request = CoachProfileRequest(gameTitle, rank, bio, hourlyRate, proofImage)
                val result = coachRepository.createCoachProfile(request)
                result.onSuccess {
                    _coachState.value = CoachState.Success("Candidature soumise avec succès")
                }.onFailure { exception ->
                    _coachState.value = CoachState.Error(exception.message ?: "Erreur lors de la soumission")
                }
            } catch (e: Exception) {
                _coachState.value = CoachState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun updateCoachProfile(
        gameTitle: String,
        rank: String,
        bio: String,
        hourlyRate: BigDecimal
    ) {
        viewModelScope.launch {
            _coachState.value = CoachState.Loading
            try {
                val request = CoachProfileRequest(gameTitle, rank, bio, hourlyRate)
                val result = coachRepository.updateCoachProfile(request)
                result.onSuccess {
                    _coachState.value = CoachState.Success("Profil mis à jour")
                }.onFailure { exception ->
                    _coachState.value = CoachState.Error(exception.message ?: "Erreur lors de la mise à jour")
                }
            } catch (e: Exception) {
                _coachState.value = CoachState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun fetchEarnings() {
        viewModelScope.launch {
            _coachState.value = CoachState.Loading
            try {
                val result = coachRepository.getCoachEarnings()
                result.onSuccess { earnings ->
                    _earnings.value = earnings
                    _coachState.value = CoachState.Success("Revenus chargés")
                }.onFailure { exception ->
                    _coachState.value = CoachState.Error(exception.message ?: "Erreur lors du chargement des revenus")
                }
            } catch (e: Exception) {
                _coachState.value = CoachState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    sealed class CoachState {
        object Idle : CoachState()
        object Loading : CoachState()
        data class Success(val message: String) : CoachState()
        data class Error(val message: String) : CoachState()
    }
}
