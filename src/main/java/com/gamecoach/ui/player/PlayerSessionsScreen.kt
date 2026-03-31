package com.gamecoach.ui.player

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
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.EmptyState
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.SessionStatusBadge
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSessionsScreen(navController: NavController, email: String = "joueur@test.com") {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Toutes", "En attente", "Acceptées", "Terminées")

    // Récupération dynamique depuis le repository (VRAI FIX)
    val allSessions = remember(email) {
        MockRepository.getSessionsForUser(email)
    }

    // Filtrage réel basé sur l'onglet
    val filteredSessions = remember(selectedTab, allSessions) {
        when (selectedTab) {
            0 -> allSessions
            1 -> allSessions.filter { it.status == SessionStatus.PENDING || it.status == SessionStatus.AWAITING_PAYMENT }
            2 -> allSessions.filter { it.status == SessionStatus.ACCEPTED }
            3 -> allSessions.filter { it.status == SessionStatus.COMPLETED }
            else -> allSessions
        }
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Mes Sessions",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    navController.navigate(Screen.PlayerHome.createRoute(email)) {
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
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (filteredSessions.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.CalendarToday,
                    message = "Aucune session dans cette catégorie"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSessions) { session ->
                        SessionCard(
                            session = session,
                            onClick = {
                                navController.navigate(Screen.SessionDetail.createRoute(session.id, email))
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
fun SessionCard(session: Session, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.game,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                SessionStatusBadge(session.status)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                Spacer(Modifier.width(4.dp))
                Text(text = session.coachName, color = TextSecondary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                Spacer(Modifier.width(4.dp))
                Text(text = "${session.scheduledDate} • ${session.scheduledTime}", color = TextSecondary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${session.duration}h", fontWeight = FontWeight.Bold)
                Text(text = "${session.amount}€", fontWeight = FontWeight.Bold, color = Success)
            }
        }
    }
}
