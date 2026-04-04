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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.EmptyState
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.player.SessionCard
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachSessionsScreen(navController: NavController, email: String = "coach@test.com") {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Toutes", "En attente", "Acceptées", "Terminées")

    // Récupération dynamique depuis le repository
    val allSessions = remember(email) {
        MockRepository.getSessionsForUser(email)
    }

    // Filtrage réel par onglet
    val filteredSessions = remember(selectedTab, allSessions) {
        when (selectedTab) {
            0 -> allSessions
            1 -> allSessions.filter { it.status == SessionStatus.PENDING || it.status == SessionStatus.AWAITING_PAYMENT }
            2 -> allSessions.filter { it.status == SessionStatus.ACCEPTED || it.status == SessionStatus.IN_PROGRESS }
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
                    navController.navigate(Screen.CoachHome.createRoute(email)) {
                        popUpTo(Screen.CoachHome.route) { inclusive = true }
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
                                navController.navigate(Screen.CoachSessionDetail.createRoute(session.id, email))
                            }
                        )
                    }
                }
            }
        }
    }
}
