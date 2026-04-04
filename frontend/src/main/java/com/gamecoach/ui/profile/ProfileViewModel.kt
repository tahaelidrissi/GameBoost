package com.gamecoach.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamecoach.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun updateProfile(username: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Appel API
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}