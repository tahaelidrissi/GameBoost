package com.gamecoach.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.gamecoach.data.MockRepository
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, email: String = "joueur@test.com") {
    // Récupération dynamique et réactive depuis le repository
    // En ajoutant MockRepository.registeredUsers en tant que clé de remember, 
    // l'UI se rafraîchira dès qu'un utilisateur est modifié dans la liste.
    val user = remember(email, MockRepository.registeredUsers) {
        MockRepository.getUserByEmail(email) ?: User(
            id = "0",
            username = email.substringBefore("@"),
            email = email,
            role = UserRole.PLAYER,
            createdAt = "Inconnu"
        )
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Mon Profil",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    val route = when (user.role) {
                        UserRole.ADMIN -> Screen.AdminHome.createRoute(email)
                        UserRole.COACH -> Screen.CoachHome.createRoute(email)
                        UserRole.PLAYER -> Screen.PlayerHome.createRoute(email)
                    }
                    navController.navigate(route) {
                        popUpTo(route) { inclusive = true }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Paramètres")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // En-tête
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                user.username.take(1).uppercase(),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Text(
                        user.username,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            when (user.role) {
                                UserRole.PLAYER -> "Joueur"
                                UserRole.COACH -> "Coach"
                                UserRole.ADMIN -> "Admin"
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Informations
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileRow(Icons.Default.Person, "Nom d'utilisateur", user.username)
                    ProfileRow(Icons.Default.Email, "Email", user.email)
                    ProfileRow(
                        Icons.Default.Shield,
                        "Rôle",
                        when (user.role) {
                            UserRole.PLAYER -> "Joueur"
                            UserRole.COACH -> "Coach"
                            UserRole.ADMIN -> "Administrateur"
                        }
                    )
                    ProfileRow(Icons.Default.CalendarToday, "Membre depuis", user.createdAt)
                }
            }

            // Action Modifier
            Card(
                onClick = { navController.navigate(Screen.EditProfile.createRoute(email)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryBlue)
                    Text("Modifier mes informations", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    } 
                },
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

@Composable
fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
