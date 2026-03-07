package com.gamecoach.ui.coach

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTextField
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.*
import com.gamecoach.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachApplicationScreen(navController: NavController) {
    var selectedGame by remember { mutableStateOf("") }
    var selectedRank by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var proofImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showGameMenu by remember { mutableStateOf(false) }
    var showRankMenu by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        proofImageUri = uri
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Candidature Coach",
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
            Text(
                text = "Rejoignez notre équipe de coachs professionnels",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Sélection du jeu
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Jeu principal", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = showGameMenu,
                        onExpandedChange = { showGameMenu = !showGameMenu }
                    ) {
                        OutlinedTextField(
                            value = selectedGame,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sélectionnez votre jeu") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGameMenu) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = showGameMenu,
                            onDismissRequest = { showGameMenu = false }
                        ) {
                            Constants.GAMES.forEach { game ->
                                DropdownMenuItem(
                                    text = { Text(game) },
                                    onClick = {
                                        selectedGame = game
                                        selectedRank = ""
                                        showGameMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Sélection du rang
            if (selectedGame.isNotEmpty()) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rang actuel", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        val ranks = com.gamecoach.util.Utils.getRanksByGame(selectedGame)

                        ExposedDropdownMenuBox(
                            expanded = showRankMenu,
                            onExpandedChange = { showRankMenu = !showRankMenu }
                        ) {
                            OutlinedTextField(
                                value = selectedRank,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sélectionnez votre rang") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRankMenu) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = showRankMenu,
                                onDismissRequest = { showRankMenu = false }
                            ) {
                                ranks.forEach { rank ->
                                    DropdownMenuItem(
                                        text = { Text(rank) },
                                        onClick = {
                                            selectedRank = rank
                                            showRankMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Biographie") },
                placeholder = { Text("Parlez de votre expérience, votre style de coaching...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6
            )

            // Tarif horaire
            GameCoachTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                label = "Tarif horaire (€)",
                leadingIcon = Icons.Default.AttachMoney
            )

            // Upload preuve
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Preuve de rang",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Téléchargez une capture d'écran de votre rang (JPG/PNG, max 5MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (proofImageUri != null) "Image sélectionnée" else "Télécharger une image")
                    }

                    if (proofImageUri != null) {
                        Text(
                            "✓ Image sélectionnée",
                            color = Success,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            GameCoachButton(
                text = "Soumettre ma candidature",
                onClick = {
                    // TODO: Envoyer la candidature
                    isLoading = true
                    navController.navigateUp()
                },
                isLoading = isLoading,
                enabled = selectedGame.isNotEmpty() &&
                        selectedRank.isNotEmpty() &&
                        bio.isNotEmpty() &&
                        hourlyRate.isNotEmpty() &&
                        proofImageUri != null
            )
        }
    }
}