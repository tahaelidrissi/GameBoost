package com.gamecoach.ui.player

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
fun PlayerHomeScreen(navController: NavController, email: String = "joueur@test.com") {
    val user = remember(email) { MockRepository.getUserByEmail(email) }
    val username = user?.username ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
    
    // Récupération dynamique des statistiques depuis le repository
    val (sessionCount, totalHours) = remember(email) { MockRepository.getUserStats(email) }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "GameBoost",
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.createRoute(email)) }) {
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
                    onClick = { navController.navigate(Screen.CoachList.route) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Coachs") },
                    label = { Text("Coachs") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PlayerSessions.createRoute(email)) },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Sessions") },
                    label = { Text("Sessions") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PlayerChatList.route) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.createRoute(email)) },
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
            // Carte de bienvenue
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bienvenue, $username !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Prêt à améliorer vos compétences ?",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Actions rapides
            Text(
                text = "Actions rapides",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.CoachList.route) },
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Search, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                        Text("Trouver un Coach", fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.PlayerSessions.createRoute(email)) },
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = Success, modifier = Modifier.size(40.dp))
                        Text("Mes Sessions", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Progression Dynamique
            Text(
                text = "Votre progression",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.CalendarToday,
                    value = "$sessionCount",
                    label = "Sessions",
                    modifier = Modifier.weight(1f),
                    iconTint = PrimaryBlue
                )

                StatCard(
                    icon = Icons.Default.Timer,
                    value = "${totalHours}h",
                    label = "de coaching",
                    modifier = Modifier.weight(1f),
                    iconTint = Success
                )
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
