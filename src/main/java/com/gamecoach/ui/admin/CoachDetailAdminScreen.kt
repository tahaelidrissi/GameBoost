package com.gamecoach.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.gamecoach.model.Coach
import com.gamecoach.model.CoachStatus
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.player.InfoRow
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDetailAdminScreen(navController: NavController) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    val coach = remember {
        Coach(
            id = "1",
            username = "ProValorant",
            email = "coach@test.com",
            game = "Valorant",
            rank = "Radiant",
            bio = "Coach professionnel avec 5 ans d'expérience",
            hourlyRate = 35.0,
            status = CoachStatus.PENDING,
            proofImageBase64 = null
        )
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Détails du coach",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profil
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
                                coach.username.first().toString(),
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
                        color = Warning.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            "⏳ En attente de validation",
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
                    Text("Informations", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Divider()
                    InfoRow(Icons.Default.Email, "Email", coach.email)
                    InfoRow(Icons.Default.SportsEsports, "Jeu", coach.game)
                    InfoRow(Icons.Default.EmojiEvents, "Rang", coach.rank)
                    InfoRow(Icons.Default.AttachMoney, "Tarif horaire", "${coach.hourlyRate}€/h")
                }
            }

            // Bio
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Biographie", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Divider()
                    Text(coach.bio, color = TextSecondary)
                }
            }

            // Preuve
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Preuve de rang", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Divider()

                    if (coach.proofImageBase64 != null) {
                        Image(
                            painter = rememberAsyncImagePainter(coach.proofImageBase64),
                            contentDescription = "Preuve de rang",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = TextSecondary
                                )
                                Text(
                                    "Aucune preuve fournie",
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showRejectDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Rejeter")
                }

                GameCoachButton(
                    text = "Approuver",
                    onClick = { showApproveDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Dialogues de confirmation
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Approuver le coach") },
            text = { Text("Voulez-vous approuver ${coach.username} comme coach ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Approuver
                        showApproveDialog = false
                        navController.navigateUp()
                    }
                ) {
                    Text("Approuver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Rejeter le coach") },
            text = { Text("Voulez-vous rejeter la candidature de ${coach.username} ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Rejeter
                        showRejectDialog = false
                        navController.navigateUp()
                    }
                ) {
                    Text("Rejeter", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
