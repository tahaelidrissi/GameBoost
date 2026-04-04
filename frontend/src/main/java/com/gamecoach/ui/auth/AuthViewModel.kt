package com.gamecoach.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                // TODO: Appel API de connexion
                // Simulation
                kotlinx.coroutines.delay(1000)

                val user = User(
                    id = "1",
                    username = "TestUser",
                    email = email,
                    role = UserRole.PLAYER
                )

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

    fun register(username: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            try {
                // TODO: Appel API d'inscription
                // Simulation
                kotlinx.coroutines.delay(1000)

                _authState.value = _authState.value.copy(
                    isLoading = false
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
        viewModelScope.launch {
            // TODO: Nettoyer le token
            _authState.value = AuthState()
        }
    }
}