package com.gameboost.frontend.data.repository

import com.gameboost.frontend.data.api.ApiService
import com.gameboost.frontend.data.models.*
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun register(
        email: String,
        password: String,
        username: String,
        role: String
    ): Result<AuthResponse> = runCatching {
        apiService.register(RegisterRequest(email, password, username, role)).data!!
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        apiService.login(LoginRequest(email, password)).data!!
    }

    suspend fun getProfile(): Result<User> = runCatching {
        apiService.getProfile().data!!
    }

    suspend fun updateProfile(username: String?, email: String?): Result<User> = runCatching {
        apiService.updateProfile(UpdateUserRequest(username, email)).data!!
    }
}

class CoachRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun getCoaches(game: String? = null, rank: String? = null): Result<List<CoachResponse>> = runCatching {
        val response = apiService.getCoaches(game, rank)
        @Suppress("UNCHECKED_CAST")
        (response.data?.get("coaches") as List<Map<String, Any>>).map { 
            it as CoachResponse 
        }
    }

    suspend fun getCoachById(coachId: Long): Result<CoachResponse> = runCatching {
        apiService.getCoachById(coachId).data!!
    }

    suspend fun createCoachProfile(request: CoachProfileRequest): Result<CoachProfile> = runCatching {
        apiService.createCoachProfile(request).data!!
    }

    suspend fun updateCoachProfile(request: CoachProfileRequest): Result<CoachProfile> = runCatching {
        apiService.updateCoachProfile(request).data!!
    }

    suspend fun getCoachEarnings(): Result<Double> = runCatching {
        val response = apiService.getCoachEarnings()
        (response.data?.get("total_earnings") as Number).toDouble()
    }
}

class SessionRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun createSession(request: SessionRequest): Result<Session> = runCatching {
        apiService.createSession(request).data!!
    }

    suspend fun getSessions(status: String? = null): Result<List<SessionResponse>> = runCatching {
        val response = apiService.getSessions(status)
        @Suppress("UNCHECKED_CAST")
        (response.data?.get("sessions") as List<Map<String, Any>>).map { 
            it as SessionResponse 
        }
    }

    suspend fun getSessionById(sessionId: Long): Result<Session> = runCatching {
        apiService.getSessionById(sessionId).data!!
    }

    suspend fun acceptSession(sessionId: Long): Result<Session> = runCatching {
        apiService.acceptSession(sessionId).data!!
    }

    suspend fun completeSession(sessionId: Long): Result<Session> = runCatching {
        apiService.completeSession(sessionId).data!!
    }

    suspend fun confirmSession(sessionId: Long): Result<Session> = runCatching {
        apiService.confirmSession(sessionId).data!!
    }

    suspend fun paySession(sessionId: Long): Result<Session> = runCatching {
        apiService.paySession(sessionId).data!!
    }

    suspend fun reviewSession(sessionId: Long, request: ReviewRequest): Result<Review> = runCatching {
        apiService.reviewSession(sessionId, request).data!!
    }
}

class MessageRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun getMessages(sessionId: Long): Result<List<MessageResponse>> = runCatching {
        val response = apiService.getMessages(sessionId)
        @Suppress("UNCHECKED_CAST")
        (response.data?.get("messages") as List<Map<String, Any>>).map { 
            it as MessageResponse 
        }
    }

    suspend fun sendMessage(sessionId: Long, request: MessageRequest): Result<Message> = runCatching {
        apiService.sendMessage(sessionId, request).data!!
    }
}

class AdminRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun getPendingCoaches(): Result<List<CoachProfile>> = runCatching {
        val response = apiService.getPendingCoaches()
        @Suppress("UNCHECKED_CAST")
        (response.data?.get("pending_coaches") as List<Map<String, Any>>).map { 
            it as CoachProfile 
        }
    }

    suspend fun approveCoach(coachId: Long): Result<User> = runCatching {
        apiService.approveCoach(coachId).data!!
    }

    suspend fun rejectCoach(coachId: Long): Result<Unit> = runCatching {
        apiService.rejectCoach(coachId)
    }

    suspend fun getDashboard(): Result<Map<String, Any>> = runCatching {
        apiService.getDashboard().data!!
    }

    suspend fun getAllUsers(): Result<List<User>> = runCatching {
        apiService.getAllUsers().data!!
    }
}
