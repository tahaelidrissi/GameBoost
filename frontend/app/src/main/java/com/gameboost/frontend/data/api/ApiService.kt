package com.gameboost.frontend.data.api

import com.gameboost.frontend.data.models.*
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @GET("users/me")
    suspend fun getProfile(): ApiResponse<User>

    @PUT("users/me")
    suspend fun updateProfile(@Body request: UpdateUserRequest): ApiResponse<User>

    // ==================== COACHES ====================
    @GET("coaches")
    suspend fun getCoaches(
        @Query("game") game: String? = null,
        @Query("rank") rank: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<Map<String, Any>>

    @GET("coaches/{id}")
    suspend fun getCoachById(@Path("id") coachId: Long): ApiResponse<CoachResponse>

    @POST("coaches/profile")
    suspend fun createCoachProfile(@Body request: CoachProfileRequest): ApiResponse<CoachProfile>

    @PUT("coaches/me/profile")
    suspend fun updateCoachProfile(@Body request: CoachProfileRequest): ApiResponse<CoachProfile>

    @GET("coaches/me/earnings")
    suspend fun getCoachEarnings(): ApiResponse<Map<String, Any>>

    // ==================== SESSIONS ====================
    @POST("sessions/request")
    suspend fun createSession(@Body request: SessionRequest): ApiResponse<Session>

    @GET("sessions")
    suspend fun getSessions(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<Map<String, Any>>

    @GET("sessions/{id}")
    suspend fun getSessionById(@Path("id") sessionId: Long): ApiResponse<Session>

    @PATCH("sessions/{id}/accept")
    suspend fun acceptSession(@Path("id") sessionId: Long): ApiResponse<Session>

    @PATCH("sessions/{id}/complete")
    suspend fun completeSession(@Path("id") sessionId: Long): ApiResponse<Session>

    @PATCH("sessions/{id}/confirm")
    suspend fun confirmSession(@Path("id") sessionId: Long): ApiResponse<Session>

    @POST("sessions/{id}/pay")
    suspend fun paySession(@Path("id") sessionId: Long): ApiResponse<Session>

    @POST("sessions/{id}/review")
    suspend fun reviewSession(
        @Path("id") sessionId: Long,
        @Body request: ReviewRequest
    ): ApiResponse<Review>

    // ==================== MESSAGES ====================
    @GET("sessions/{id}/messages")
    suspend fun getMessages(@Path("id") sessionId: Long): ApiResponse<Map<String, Any>>

    @POST("sessions/{id}/messages")
    suspend fun sendMessage(
        @Path("id") sessionId: Long,
        @Body request: MessageRequest
    ): ApiResponse<Message>

    // ==================== ADMIN ====================
    @GET("admin/coaches/pending")
    suspend fun getPendingCoaches(): ApiResponse<Map<String, Any>>

    @PATCH("admin/coaches/{id}/approve")
    suspend fun approveCoach(@Path("id") coachId: Long): ApiResponse<User>

    @PATCH("admin/coaches/{id}/reject")
    suspend fun rejectCoach(@Path("id") coachId: Long): ApiResponse<Void>

    @GET("admin/dashboard")
    suspend fun getDashboard(): ApiResponse<Map<String, Any>>

    @GET("admin/users")
    suspend fun getAllUsers(): ApiResponse<List<User>>
}
