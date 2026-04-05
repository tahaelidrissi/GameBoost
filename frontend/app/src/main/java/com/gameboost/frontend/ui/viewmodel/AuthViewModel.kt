package com.gameboost.frontend.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameboost.frontend.data.models.*
import com.gameboost.frontend.data.repository.AuthRepository
import com.gameboost.frontend.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun register(email: String, password: String, username: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.register(email, password, username, role)
                result.onSuccess { authResponse ->
                    tokenManager.saveToken(authResponse.token, authResponse.user.role.name, authResponse.user.id.toString())
                    _currentUser.value = authResponse.user
                    _authState.value = AuthState.Success("Inscription réussie")
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erreur lors de l'inscription")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.login(email, password)
                result.onSuccess { authResponse ->
                    tokenManager.saveToken(authResponse.token, authResponse.user.role.name, authResponse.user.id.toString())
                    _currentUser.value = authResponse.user
                    _authState.value = AuthState.Success("Connexion réussie")
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Email ou mot de passe incorrect")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.getProfile()
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Profil récupéré")
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erreur lors de la récupération du profil")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun updateProfile(username: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.updateProfile(username, email)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success("Profil mis à jour")
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erreur lors de la mise à jour")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearToken()
                _currentUser.value = null
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erreur lors de la déconnexion")
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
