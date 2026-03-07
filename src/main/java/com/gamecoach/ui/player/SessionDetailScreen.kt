package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(navController: NavController) {
    val session = remember {
        Session(
            id = "1",
            coachName = "ProValorant",
            game = "Valorant",
            scheduledDate = "2024-03-10",
            scheduledTime = "16:00",
            duration = 1,
            amount = 35.0,
            status = SessionStatus.ACCEPTED,
            isPaid = false
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
            // Statut
            SessionStatusBadge(session.status)

            // Informations
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(Icons.Default.SportsEsports, "Jeu", session.game)
                    InfoRow(Icons.Default.Person, "Coach", session.coachName)
                    InfoRow(Icons.Default.CalendarToday, "Date", session.scheduledDate)
                    InfoRow(Icons.Default.AccessTime, "Heure", session.scheduledTime)
                    InfoRow(Icons.Default.Timer, "Durée", "${session.duration}h")
                    InfoRow(Icons.Default.AttachMoney, "Montant", "${session.amount}€")
                }
            }

            Spacer(Modifier.weight(1f))

            // Actions
            if (session.status == SessionStatus.ACCEPTED && !session.isPaid) {
                GameCoachButton(
                    text = "Payer la session",
                    onClick = {
                        navController.navigate(Screen.Payment.createRoute(session.id))
                    }
                )
            }

            if (session.status == SessionStatus.COMPLETED) {
                GameCoachButton(
                    text = "Évaluer le coach",
                    onClick = {
                        navController.navigate(Screen.ReviewCoach.createRoute(session.id))
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.Chat.createRoute(session.id))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Chat, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Discuter avec le coach")
            }
        }
    }
}
