package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.InfoRow
import com.gamecoach.ui.components.SessionStatusBadge
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachSessionDetailScreen(navController: NavController, sessionId: String = "1", email: String = "") {
    var refreshTrigger by remember { mutableStateOf(0) }
    val session = remember(sessionId, refreshTrigger) {
        MockRepository.getSessionById(sessionId)
    }

    if (session == null) {
        Scaffold { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Session introuvable")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Détails de la session",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SessionStatusBadge(session.status)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(Icons.Default.Person, "Joueur", session.playerName)
                    InfoRow(Icons.Default.SportsEsports, "Jeu", session.game)
                    InfoRow(Icons.Default.CalendarToday, "Date", session.scheduledDate)
                    InfoRow(Icons.Default.AccessTime, "Heure", session.scheduledTime)
                    InfoRow(Icons.Default.Timer, "Durée", "${session.duration}h")
                    InfoRow(Icons.Default.AttachMoney, "Montant", "${session.amount}€")
                }
            }

            Spacer(Modifier.weight(1f))

            if (session.status == SessionStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            MockRepository.updateSessionStatus(session.id, SessionStatus.CANCELLED)
                            refreshTrigger++
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                    ) {
                        Text("Refuser")
                    }
                    GameCoachButton(
                        text = "Accepter",
                        onClick = { 
                            MockRepository.validateSessionByCoach(session.id)
                            refreshTrigger++
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (session.status == SessionStatus.AWAITING_PAYMENT) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        "En attente du paiement par le joueur...",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (session.status == SessionStatus.ACCEPTED) {
                GameCoachButton(
                    text = "Terminer la session",
                    onClick = { 
                        MockRepository.completeSession(session.id)
                        refreshTrigger++
                    }
                )
            }

            if (session.status != SessionStatus.CANCELLED && session.status != SessionStatus.REJECTED) {
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.CoachChat.createRoute(session.id, email))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Discuter avec le joueur")
                }
            }
        }
    }
}
