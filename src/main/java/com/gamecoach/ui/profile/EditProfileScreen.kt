package com.gamecoach.ui.profile

import androidx.compose.foundation.layout.*
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
fun EditProfileScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameCoachTextField(
                value = username,
                onValueChange = { username = it },
                label = "Nom d'utilisateur",
                leadingIcon = Icons.Default.Person
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