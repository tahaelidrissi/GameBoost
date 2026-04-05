package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachProfileScreen(navController: NavController, email: String = "coach@test.com") {
    val username = email.substringBefore("@").replaceFirstChar { it.uppercase() }
    val coach = remember(email) {
        Coach(
            id = "1",
            username = username,
            email = email,
            game = "Valorant",
            rank = "Radiant",
            bio = "Coach professionnel avec 5 ans d'expérience",
            hourlyRate = 35.0,
            status = CoachStatus.APPROVED,
            rating = 5.0,
            totalReviews = 2,
            totalSessions = 2,
            totalEarnings = 70.0
        )
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Mon Profil",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    navController.navigate(Screen.CoachHome.createRoute(email)) {
                        popUpTo(Screen.CoachHome.route) { inclusive = true }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EditCoachProfile.route) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier")
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
                                username.take(1).uppercase(),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Text(
                        coach.username,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        color = when (coach.status) {
                            CoachStatus.APPROVED -> Success
                            CoachStatus.PENDING -> Warning
                            CoachStatus.REJECTED -> Error
                        }.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = when (coach.status) {
                                CoachStatus.APPROVED -> "✓ Approuvé"
                                CoachStatus.PENDING -> "⏳ En attente"
                                CoachStatus.REJECTED -> "✗ Rejeté"
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
                    ProfileInfoRow(Icons.Default.Person, "Nom d'utilisateur", coach.username)
                    ProfileInfoRow(Icons.Default.Email, "Email", coach.email)
                    ProfileInfoRow(Icons.Default.SportsEsports, "Jeu", coach.game)
                    ProfileInfoRow(Icons.Default.EmojiEvents, "Rang", coach.rank)
                    ProfileInfoRow(Icons.Default.AttachMoney, "Tarif horaire", "${coach.hourlyRate}€/h")
                }
            }

            // Bio
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Biographie", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(coach.bio, color = TextSecondary)
                }
            }

            // Note
            if (coach.totalReviews > 0) {
                Card(
                    onClick = { navController.navigate(Screen.Reviews.route) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Warning)
                            Text("Note moyenne", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "${coach.rating} (${coach.totalReviews} avis)",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
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
                Text("Retour au Menu Principal")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
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