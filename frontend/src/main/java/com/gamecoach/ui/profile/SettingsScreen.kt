package com.gamecoach.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.Error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("Français") }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Paramètres",
                onBackClick = { navController.navigateUp() }
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
            // Notifications
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Column {
                            Text("Notifications", fontWeight = FontWeight.Bold)
                            Text(
                                "Recevoir des notifications",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            // Langue
            Card(onClick = { showLanguageDialog = true }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Language, contentDescription = null)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Langue", fontWeight = FontWeight.Bold)
                        Text(selectedLanguage, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            // À propos
            Card(onClick = { showAboutDialog = true }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("À propos", fontWeight = FontWeight.Bold)
                        Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            Spacer(Modifier.weight(1f))

            // Déconnexion
            GameCoachButton(
                text = "Se déconnecter",
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error
                )
            )
        }
    }

    // Dialogues
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Sélectionner la langue") },
            text = {
                Column {
                    listOf("Français", "English", "Español").forEach { lang ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedLanguage == lang, onClick = { selectedLanguage = lang; showLanguageDialog = false })
                            Text(lang, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("À propos de GameBoost") },
            text = { 
                Text("GameBoost est la plateforme ultime pour connecter joueurs et coachs professionnels.\n\nVersion: 1.0.0\nDéveloppé par GameBoost Team.") 
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Fermer") }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Déconnexion") },
            text = { Text("Voulez-vous vraiment vous déconnecter ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Déconnexion", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
