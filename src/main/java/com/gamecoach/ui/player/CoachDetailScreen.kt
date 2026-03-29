package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.gamecoach.model.Review
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDetailScreen(navController: NavController, email: String = "joueur@test.com") {
    // Simulation - récupérer le coach par ID
    val coach = remember {
        Coach(
            id = "1",
            username = "ProValorant",
            game = "Valorant",
            rank = "Radiant",
            bio = "Coach professionnel Valorant avec plus de 5 ans d'expérience. Spécialisé dans le game sense et le positionnement. J'ai aidé plus de 100 joueurs à atteindre Immortal+.",
            hourlyRate = 35.0,
            rating = 4.8,
            totalReviews = 42,
            totalSessions = 150,
            status = CoachStatus.APPROVED
        )
    }

    val reviews = remember {
        listOf(
            Review(
                id = "1",
                playerName = "Player1",
                rating = 5,
                comment = "Excellent coach ! J'ai progressé énormément en seulement 3 sessions.",
                createdAt = "2024-02-28"
            ),
            Review(
                id = "2",
                playerName = "GamerPro",
                rating = 5,
                comment = "Très professionnel et patient. Explications claires.",
                createdAt = "2024-02-25"
            ),
            Review(
                id = "3",
                playerName = "NoobMaster",
                rating = 4,
                comment = "Bon coach, quelques sessions et j'ai vu la différence.",
                createdAt = "2024-02-20"
            )
        )
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Profil du Coach",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    navController.navigate(Screen.PlayerHome.createRoute(email)) {
                        popUpTo(Screen.PlayerHome.route) { inclusive = true }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GameCoachButton(
                        text = "Demander une session",
                        onClick = {
                            navController.navigate(Screen.RequestSession.createRoute(coach.id))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // En-tête du profil
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = coach.username.first().toString(),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }

                        Text(
                            text = coach.username,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = coach.game,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text("•", color = Color.White.copy(alpha = 0.9f))
                            Text(
                                text = coach.rank,
                                color = Color.White,
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
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = String.format("%.1f", coach.rating),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "(${coach.totalReviews} avis)",
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Statistiques
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${coach.totalSessions}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Sessions totales",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${coach.hourlyRate.toInt()}€/h",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Success
                            )
                            Text(
                                text = "Tarif horaire",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Bio
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "À propos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = coach.bio,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Avis
            item {
                Text(
                    text = "Évaluations (${reviews.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            items(reviews) { review ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
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
                                text = review.playerName,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(review.rating) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Warning,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = review.comment,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = review.createdAt,
                            fontSize = 12.sp,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
