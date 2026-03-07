package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.model.Coach
import com.gamecoach.model.CoachStatus
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*
import com.gamecoach.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachListScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<String?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Données simulées
    val coaches = remember {
        listOf(
            Coach(
                id = "1",
                username = "ProValorant",
                game = "Valorant",
                rank = "Radiant",
                hourlyRate = 35.0,
                rating = 4.8,
                totalReviews = 42,
                status = CoachStatus.APPROVED
            ),
            Coach(
                id = "2",
                username = "LeagueGuru",
                game = "League of Legends",
                rank = "Challenger",
                hourlyRate = 40.0,
                rating = 4.9,
                totalReviews = 87,
                status = CoachStatus.APPROVED
            ),
            Coach(
                id = "3",
                username = "CS2Expert",
                game = "Counter-Strike 2",
                rank = "Global Elite",
                hourlyRate = 30.0,
                rating = 4.7,
                totalReviews = 35,
                status = CoachStatus.APPROVED
            ),
            Coach(
                id = "4",
                username = "OverwatchPro",
                game = "Overwatch 2",
                rank = "Grandmaster",
                hourlyRate = 25.0,
                rating = 4.6,
                totalReviews = 28,
                status = CoachStatus.APPROVED
            )
        )
    }

    val filteredCoaches = coaches.filter { coach ->
        (selectedGame == null || coach.game == selectedGame) &&
                (searchQuery.isEmpty() || coach.username.contains(searchQuery, ignoreCase = true) ||
                        coach.game.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Trouver un Coach",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barre de recherche et filtre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Rechercher par nom, jeu ou rang...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                FilledTonalButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrer")
                }
            }

            // Chips de jeux
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedGame == null,
                        onClick = { selectedGame = null },
                        label = { Text("Tous les jeux") }
                    )
                }
                items(Constants.GAMES) { game ->
                    FilterChip(
                        selected = selectedGame == game,
                        onClick = { selectedGame = game },
                        label = { Text(game) }
                    )
                }
            }

            Text(
                text = "${filteredCoaches.size} coachs disponibles",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Liste des coachs
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCoaches) { coach ->
                    CoachCard(
                        coach = coach,
                        onClick = {
                            navController.navigate(Screen.CoachDetail.createRoute(coach.id))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachCard(coach: Coach, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = coach.username.first().toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            // Informations
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = coach.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = coach.game,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text("•", color = TextSecondary)
                    Text(
                        text = coach.rank,
                        fontSize = 14.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.1f", coach.rating),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${coach.totalReviews})",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text("•", color = TextSecondary)
                    Text(
                        text = "${coach.hourlyRate.toInt()}€/h",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Success
                    )
                }
            }

            // Bouton
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}
