package com.gamecoach.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTextField
import com.gamecoach.ui.components.GameCoachTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCoachProfileScreen(navController: NavController) {
    var bio by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Modifier le profil",
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
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Biographie") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            GameCoachTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                label = "Tarif horaire (€)",
                leadingIcon = Icons.Default.AttachMoney
            )

            Spacer(Modifier.weight(1f))

            GameCoachButton(
                text = "Sauvegarder",
                onClick = {
                    isLoading = true
                    // TODO: Mise à jour
                    navController.navigateUp()
                },
                isLoading = isLoading
            )
        }
    }
}