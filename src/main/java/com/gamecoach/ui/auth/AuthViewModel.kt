package com.gamecoach.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Coach
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import com.gamecoach.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            try {
                // Simulation d'un délai réseau
                kotlinx.coroutines.delay(800)

                val user = MockRepository.getUserByEmail(email)
                
                if (user == null) {
                    _authState.value = _authState.value.copy(isLoading = false, error = "Utilisateur non trouvé")
                    return@launch
                }

                if (!MockRepository.verifyPassword(email, password)) {
                    _authState.value = _authState.value.copy(isLoading = false, error = "Mot de passe incorrect")
                    return@launch
                }

                if (user.status != UserStatus.APPROVED) {
                    _authState.value = _authState.value.copy(
                        isLoading = false, 
                        error = "Votre compte est en attente de validation par l'administrateur"
                    )
                    return@launch
                }

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    user = user,
                    isAuthenticated = true
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun register(username: String, email: String, password: String, role: UserRole, game: String? = null, rank: String? = null) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            try {
                kotlinx.coroutines.delay(800)
                
                if (MockRepository.isUserRegistered(email)) {
                    _authState.value = _authState.value.copy(isLoading = false, error = "Cet email est déjà utilisé")
                    return@launch
                }

                val userId = UUID.randomUUID().toString()
                val newUser = User(
                    id = userId,
                    username = username,
                    email = email,
                    role = role,
                    status = UserStatus.PENDING
                )

                val coachInfo = if (role == UserRole.COACH) {
                    Coach(
                        id = userId,
                        username = username,
                        email = email,
                        game = game ?: "",
                        rank = rank ?: "",
                        hourlyRate = 25.0 // Tarif par défaut
                    )
                } else null

                MockRepository.registerUser(newUser, coachInfo)

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Inscription réussie ! En attente de validation par l'admin."
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun logout() {
        _authState.value = AuthState()
    }
}
