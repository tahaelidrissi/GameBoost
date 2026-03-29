package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
fun SessionDetailScreen(navController: NavController, sessionId: String = "1") {
    // Récupération dynamique de la session depuis le "Backend" simulé
    val session = remember(sessionId) {
        MockRepository.getSessionById(sessionId)
    }

    if (session == null) {
        // Gérer le cas où la session n'existe pas
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
                onBackClick = { navController.navigateUp() },
                onHomeClick = { 
                    navController.navigate(Screen.PlayerHome.createRoute("joueur@test.com")) {
                        popUpTo(Screen.PlayerHome.route) { inclusive = true }
                    }
                }
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
            
            if (session.isPaid) {
                Surface(
                    color = Success.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success)
                        Text("Session payée avec succès", color = Success, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Informations spécifiques à cette session
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

            // Actions Dynamiques : Affichage selon les règles métier
            
            // RÈGLE : Le bouton payer n'apparaît QUE si la session est ACCEPTÉE et NON PAYÉE
            if (MockRepository.canPaySession(session.id)) {
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
