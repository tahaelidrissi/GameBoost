package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.PrimaryBlue
import com.gamecoach.ui.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewCoachScreen(navController: NavController, sessionId: String = "") {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    
    val session = remember(sessionId) { MockRepository.getSessionById(sessionId) }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Évaluer le coach",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Session avec ${session?.coachName ?: "votre coach"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Comment s'est passée votre session ?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 }
                            ) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = if (index < rating) Warning else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    if (rating > 0) {
                        Text(
                            text = when (rating) {
                                1 -> "Très mauvais"
                                2 -> "Mauvais"
                                3 -> "Moyen"
                                4 -> "Bon"
                                5 -> "Excellent"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Commentaire (optionnel)") },
                minLines = 4,
                maxLines = 6,
                placeholder = { Text("Partagez votre expérience...") }
            )

            Spacer(Modifier.weight(1f))

            GameCoachButton(
                text = "Confirmer l'évaluation",
                onClick = {
                    // MISE À JOUR RÉELLE DU SCORE DU COACH
                    if (session != null) {
                        MockRepository.updateCoachRating(session.coachName, rating)
                    }
                    navController.navigateUp()
                },
                enabled = rating > 0
            )
        }
    }
}
