package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(navController: NavController, email: String = "") {
    // Utilisation des données réelles du repository
    val sessions = remember(email) { MockRepository.getSessionsForUser(email) }
    val paidSessions = sessions.filter { it.isPaid }
    
    val stats = remember(email) { MockRepository.getUserStats(email) }
    val totalRevenue = stats.second
    val completedSessions = stats.first
    val totalHours = sessions.filter { it.status == SessionStatus.COMPLETED }.sumOf { it.duration }
    
    val user = remember(email) { MockRepository.getUserByEmail(email) }
    val coach = remember(user) { user?.let { MockRepository.getCoachById(it.id) } }
    val hourlyRate = coach?.hourlyRate ?: 0.0

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Mes Revenus",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Résumé
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Success)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Total gagné",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        "${totalRevenue}€",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Divider(color = Color.White.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Sessions", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("$completedSessions", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Heures", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${totalHours}h", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Tarif", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${hourlyRate.toInt()}€/h", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Historique
            Text(
                "Historique des paiements",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (paidSessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun paiement reçu pour le moment", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(paidSessions) { session ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(session.game, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Avec ${session.playerName}",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "${session.scheduledDate} • ${session.duration}h",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    "+${session.amount}€",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Success
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
