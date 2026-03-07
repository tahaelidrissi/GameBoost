package com.gamecoach.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.StatCard
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Dashboard Admin",
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PendingCoaches.route) },
                    icon = { Icon(Icons.Default.HourglassEmpty, contentDescription = "Coachs") },
                    label = { Text("Coachs") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.ManageUsers.route) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Utilisateurs") },
                    label = { Text("Utilisateurs") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // En-tête
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Dashboard Admin",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Vue d'ensemble de la plateforme",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Stats
            Text("Statistiques", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.People,
                    value = "4",
                    label = "Utilisateurs",
                    modifier = Modifier.weight(1f),
                    iconTint = PrimaryBlue
                )
                StatCard(
                    icon = Icons.Default.School,
                    value = "5",
                    label = "Coachs actifs",
                    modifier = Modifier.weight(1f),
                    iconTint = Success
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.CalendarToday,
                    value = "1",
                    label = "Sessions terminées",
                    modifier = Modifier.weight(1f),
                    iconTint = Warning
                )
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    value = "70€",
                    label = "Revenus totaux",
                    modifier = Modifier.weight(1f),
                    iconTint = Success
                )
            }

            // Actions rapides
            Text("Actions rapides", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Card(
                onClick = { navController.navigate(Screen.PendingCoaches.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text("Coachs en attente", fontWeight = FontWeight.Bold)
                        Text("Valider les candidatures", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            }

            Card(
                onClick = { navController.navigate(Screen.ManageUsers.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text("Gérer les utilisateurs", fontWeight = FontWeight.Bold)
                        Text("Voir tous les utilisateurs", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
