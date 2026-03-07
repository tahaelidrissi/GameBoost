package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.InfoRow
import com.gamecoach.ui.components.SessionStatusBadge
import com.gamecoach.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachSessionDetailScreen(navController: NavController) {
    val session = remember {
        Session(
            id = "1",
            playerName = "joueur1",
            game = "Valorant",
            scheduledDate = "2024-03-10",
            scheduledTime = "16:00",
            duration = 1,
            amount = 35.0,
            status = SessionStatus.PENDING
        )
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
                        onClick = { /* TODO: Refuser */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refuser")
                    }
                    GameCoachButton(
                        text = "Accepter",
                        onClick = { /* TODO: Accepter */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (session.status == SessionStatus.ACCEPTED) {
                GameCoachButton(
                    text = "Terminer la session",
                    onClick = { /* TODO: Terminer */ }
                )
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.CoachChat.createRoute(session.id))
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
