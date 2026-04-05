package com.gameboost.frontend.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameboost.frontend.data.models.*
import com.gameboost.frontend.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _messageState = MutableLiveData<MessageState>(MessageState.Idle)
    val messageState: LiveData<MessageState> = _messageState

    private val _messages = MutableLiveData<List<MessageResponse>>()
    val messages: LiveData<List<MessageResponse>> = _messages

    fun getMessages(sessionId: Long) {
        viewModelScope.launch {
            _messageState.value = MessageState.Loading
            try {
                val result = messageRepository.getMessages(sessionId)
                result.onSuccess { messageList ->
                    _messages.value = messageList
                    _messageState.value = MessageState.Success("Messages chargés")
                }.onFailure { exception ->
                    _messageState.value = MessageState.Error(exception.message ?: "Erreur lors du chargement")
                }
            } catch (e: Exception) {
                _messageState.value = MessageState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun sendMessage(sessionId: Long, content: String) {
        viewModelScope.launch {
            _messageState.value = MessageState.Loading
            try {
                val request = MessageRequest(content)
                val result = messageRepository.sendMessage(sessionId, request)
                result.onSuccess {
                    _messageState.value = MessageState.Success("Message envoyé")
                    getMessages(sessionId) // Refetch messages
                }.onFailure { exception ->
                    _messageState.value = MessageState.Error(exception.message ?: "Erreur lors de l'envoi")
                }
            } catch (e: Exception) {
                _messageState.value = MessageState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    sealed class MessageState {
        object Idle : MessageState()
        object Loading : MessageState()
        data class Success(val message: String) : MessageState()
        data class Error(val message: String) : MessageState()
    }
}
