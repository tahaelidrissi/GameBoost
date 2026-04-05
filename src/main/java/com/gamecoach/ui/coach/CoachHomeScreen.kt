package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.gamecoach.data.MockRepository
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.StatCard
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachHomeScreen(navController: NavController, email: String = "coach@test.com") {
    // FIX: Utilisation du MockRepository pour le nom et les stats
    val user = remember(email) { MockRepository.getUserByEmail(email) }
    val username = user?.username ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
    
    val (sessionCount, totalHours) = remember(email) { MockRepository.getUserStats(email) }
    val revenue = sessionCount * 35 // Simulation simple de revenu

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "GameBoost",
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CoachProfile.createRoute(email)) }) {
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
                    onClick = { navController.navigate(Screen.CoachSessions.createRoute(email)) },
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
                    onClick = { navController.navigate(Screen.CoachProfile.createRoute(email)) },
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
                .verticalScroll(rememberScrollState())
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
                        "Bonjour, $username ! 👋",
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

            // Stats DYNAMIQUES
            Text("Vos statistiques", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.CalendarToday,
                    value = "$sessionCount",
                    label = "Sessions totales",
                    modifier = Modifier.weight(1f),
                    iconTint = PrimaryBlue
                )
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    value = "${revenue}€",
                    label = "Revenus",
                    modifier = Modifier.weight(1f),
                    iconTint = Success
                )
            }

            // Actions rapides
            Text("Actions rapides", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Card(
                onClick = { navController.navigate(Screen.CoachSessions.createRoute(email)) },
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

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Se déconnecter")
            }
        }
    }
}
