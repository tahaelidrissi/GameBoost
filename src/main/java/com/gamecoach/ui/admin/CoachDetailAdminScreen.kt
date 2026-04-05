package com.gamecoach.ui.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.CoachStatus
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.components.InfoRow
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDetailAdminScreen(navController: NavController, coachId: String = "") {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    // Récupération dynamique
    val coach = remember(coachId) {
        MockRepository.getCoachById(coachId)
    }

    if (coach == null) {
        Scaffold { padding -> 
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { 
                Text("Coach non trouvé") 
            } 
        }
        return
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
                        color = if (coach.status == CoachStatus.PENDING) Warning.copy(alpha = 0.2f) else Success.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            if (coach.status == CoachStatus.PENDING) "⏳ En attente de validation" else "✅ Approuvé",
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

            Spacer(Modifier.height(8.dp))

            // Actions (Affichées seulement si PENDING)
            if (coach.status == CoachStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
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
                        // ACTION RÉELLE : On approuve l'utilisateur par son email
                        MockRepository.approveUser(coach.email)
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
}
