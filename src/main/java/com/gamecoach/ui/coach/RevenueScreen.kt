package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.*

data class RevenueItem(
    val game: String,
    val playerName: String,
    val date: String,
    val duration: Int,
    val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(navController: NavController) {
    val revenues = remember {
        listOf(
            RevenueItem("Valorant", "joueur1", "28 févr. 2026", 2, 70.0)
        )
    }

    val totalRevenue = revenues.sumOf { it.amount }
    val totalHours = revenues.sumOf { it.duration }
    val completedSessions = revenues.size
    val hourlyRate = if (totalHours > 0) totalRevenue / totalHours else 0.0

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
                            Text("Sessions complétées", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("$completedSessions", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Heures totales", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${totalHours}h", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Tarif horaire", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${hourlyRate.toInt()}€/h", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Historique
            Text(
                "Historique",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(revenues) { revenue ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(revenue.game, fontWeight = FontWeight.Bold)
                                Text(
                                    "Avec ${revenue.playerName}",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    "${revenue.date} • ${revenue.duration}h",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                "+${revenue.amount}€",
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