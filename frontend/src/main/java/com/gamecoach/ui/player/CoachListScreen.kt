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
import com.gamecoach.ui.components.EmptyState
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*
import com.gamecoach.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachListScreen(navController: NavController, email: String = "joueur@test.com") {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<String?>(null) }

    // Simuler une liste qui contient des coachs avec différents statuts
    val allCoaches = remember {
        mutableStateListOf(
            Coach(id = "1", username = "ProValorant", game = "Valorant", status = CoachStatus.APPROVED, rating = 4.8, hourlyRate = 35.0, rank = "Radiant"),
            Coach(id = "2", username = "NewbieCoach", game = "LoL", status = CoachStatus.PENDING) // Ne doit pas s'afficher
        )
    }

    // Filtrer pour ne garder QUE les coachs approuvés et ceux qui correspondent à la recherche
    val visibleCoaches = allCoaches.filter { coach ->
        coach.status == CoachStatus.APPROVED &&
        (selectedGame == null || coach.game == selectedGame) &&
        (searchQuery.isEmpty() || coach.username.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Trouver un Coach",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (visibleCoaches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.School,
                        message = "Aucun coach disponible pour le moment."
                    )
                }
            } else {
                // Barre de recherche
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Rechercher un coach...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )

                // Liste complète
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleCoaches) { coach ->
                        CoachCard(
                            coach = coach,
                            onClick = {
                                navController.navigate(Screen.CoachDetail.createRoute(coach.id, email))
                            }
                        )
                    }
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.size(56.dp).clip(CircleShape), color = PrimaryBlue.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(coach.username.first().toString(), fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(coach.username, fontWeight = FontWeight.Bold)
                Text("${coach.game} • ${coach.rank}", color = TextSecondary, fontSize = 14.sp)
                Text("${coach.hourlyRate}€/h", color = Success, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}
