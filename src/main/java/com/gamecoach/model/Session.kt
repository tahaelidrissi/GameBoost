package com.gamecoach.model

data class Session(
    val id: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val coachId: String = "",
    val coachName: String = "",
    val game: String = "",
    val duration: Int = 0,
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val amount: Double = 0.0,
    val status: SessionStatus = SessionStatus.PENDING,
    val createdAt: String = "",
    val isPaid: Boolean = false
)

enum class SessionStatus {
    PENDING,            // Joueur a demandé, Coach doit valider
    AWAITING_PAYMENT,   // Coach a validé, Joueur doit payer
    ACCEPTED,           // Payé, prête à être effectuée
    IN_PROGRESS,        // En cours
    COMPLETED,          // Terminée par le coach
    REJECTED,
    CANCELLED
}
