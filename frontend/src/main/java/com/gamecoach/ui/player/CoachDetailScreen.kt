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
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Coach
import com.gamecoach.model.Review
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDetailScreen(navController: NavController, coachId: String, email: String = "") {
    // Récupération du VRAI coach via son ID
    val coach = remember(coachId) {
        MockRepository.getCoachById(coachId)
    }

    if (coach == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profil du coach introuvable")
        }
        return
    }

    // Simulation d'avis vides pour un nouveau coach
    val reviews = remember { emptyList<Review>() }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Profil de ${coach.username}",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                Row(modifier = Modifier.padding(16.dp)) {
                    GameCoachButton(
                        text = "Demander une session (${coach.hourlyRate}€/h)",
                        onClick = {
                            navController.navigate(Screen.RequestSession.createRoute(coach.id, email))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryBlue)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(modifier = Modifier.size(100.dp).clip(CircleShape), color = Color.White) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(coach.username.take(1).uppercase(), fontSize = 42.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                        Text(coach.username, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${coach.game} • ${coach.rank}", color = Color.White.copy(alpha = 0.9f))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Warning, modifier = Modifier.size(20.dp))
                            Text(String.format(" %.1f", coach.rating), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(" (${coach.totalReviews} avis)", color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("À propos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(coach.bio.ifBlank { "Ce coach n'a pas encore rempli sa biographie." }, color = TextSecondary)
                    }
                }
            }
        }
    }
}
