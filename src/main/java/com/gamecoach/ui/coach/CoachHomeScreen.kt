package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CoachHomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "GameCoach",
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CoachProfile.route) }) {
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.CoachSessions.route) },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Sessions") },
                    label = { Text("Sessions") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.CoachChatList.route) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Revenue.route) },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Revenus") },
                    label = { Text("Revenus") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.CoachProfile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Bienvenue
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Bonjour, Coach ! 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Tableau de bord coach",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Stats
            Text("Vos statistiques", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.CalendarToday,
                    value = "2",
                    label = "Sessions totales",
                    modifier = Modifier.weight(1f),
                    iconTint = PrimaryBlue
                )
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    value = "70€",
                    label = "Revenus",
                    modifier = Modifier.weight(1f),
                    iconTint = Success
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Star,
                    value = "5.0",
                    label = "Note moyenne",
                    modifier = Modifier.weight(1f),
                    iconTint = Warning
                )
                StatCard(
                    icon = Icons.Default.Timer,
                    value = "0",
                    label = "En attente",
                    modifier = Modifier.weight(1f),
                    iconTint = Warning
                )
            }

            // Actions rapides
            Text("Actions rapides", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Card(
                onClick = { navController.navigate(Screen.CoachSessions.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text("Gérer mes sessions", fontWeight = FontWeight.Bold)
                        Text("Voir et gérer toutes vos sessions", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            }

            Card(
                onClick = { navController.navigate(Screen.Revenue.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text("Mes revenus", fontWeight = FontWeight.Bold)
                        Text("Consulter l'historique des gains", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}