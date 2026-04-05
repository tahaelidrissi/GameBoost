package com.gamecoach.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Coach
import com.gamecoach.model.CoachStatus
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTextField
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.PrimaryBlue
import com.gamecoach.ui.theme.PrimaryPurple
import com.gamecoach.util.Constants
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.PLAYER) }
    
    // Coach specific fields
    var coachGame by remember { mutableStateOf("") }
    var coachRank by remember { mutableStateOf("") }
    var coachHourlyRate by remember { mutableStateOf("") }
    var isGameDropdownExpanded by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryPurple)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "GameBoost",
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Créer un compte",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Rejoignez GameBoost",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card d'inscription
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameCoachTextField(
                        value = username,
                        onValueChange = { 
                            username = it
                            if (errorMessage.isNotEmpty()) errorMessage = ""
                        },
                        label = "Nom d'utilisateur",
                        leadingIcon = Icons.Default.Person
                    )

                    GameCoachTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (errorMessage.isNotEmpty()) errorMessage = ""
                        },
                        label = "Email",
                        leadingIcon = Icons.Default.Email
                    )

                    GameCoachTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            if (errorMessage.isNotEmpty()) errorMessage = ""
                        },
                        label = "Mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Text(
                        text = "Je suis un",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedRole == UserRole.PLAYER,
                            onClick = { selectedRole = UserRole.PLAYER },
                            label = { Text("Joueur") },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = selectedRole == UserRole.COACH,
                            onClick = { selectedRole = UserRole.COACH },
                            label = { Text("Coach") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Coach specific UI
                    if (selectedRole == UserRole.COACH) {
                        Divider()
                        Text(
                            text = "Informations de Coaching",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Game Selection Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isGameDropdownExpanded,
                            onExpandedChange = { isGameDropdownExpanded = !isGameDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = coachGame,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sélectionnez votre jeu") },
                                leadingIcon = { Icon(Icons.Default.Gamepad, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGameDropdownExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = isGameDropdownExpanded,
                                onDismissRequest = { isGameDropdownExpanded = false }
                            ) {
                                Constants.GAMES.forEach { game ->
                                    DropdownMenuItem(
                                        text = { Text(game) },
                                        onClick = {
                                            coachGame = game
                                            isGameDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        GameCoachTextField(
                            value = coachRank,
                            onValueChange = { coachRank = it },
                            label = "Votre Rang (ex: Radiant)",
                            leadingIcon = Icons.Default.EmojiEvents
                        )

                        GameCoachTextField(
                            value = coachHourlyRate,
                            onValueChange = { 
                                if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                                    coachHourlyRate = it 
                                }
                            },
                            label = "Prix par heure ($)",
                            leadingIcon = Icons.Default.Payments,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GameCoachButton(
                        text = "Créer mon compte",
                        onClick = {
                            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Veuillez remplir tous les champs"
                            } else if (selectedRole == UserRole.COACH && (coachGame.isBlank() || coachRank.isBlank() || coachHourlyRate.isBlank())) {
                                errorMessage = "Veuillez préciser votre jeu, votre rang et votre tarif"
                            } else if (password.length < 8) {
                                errorMessage = "Le mot de passe doit contenir au moins 8 caractères"
                            } else {
                                isLoading = true
                                val userId = UUID.randomUUID().toString()
                                
                                val coachInfo = if (selectedRole == UserRole.COACH) {
                                    Coach(
                                        id = UUID.randomUUID().toString(),
                                        userId = userId,
                                        username = username,
                                        email = email,
                                        game = coachGame,
                                        rank = coachRank,
                                        status = CoachStatus.PENDING,
                                        hourlyRate = coachHourlyRate.toDoubleOrNull() ?: 20.0
                                    )
                                } else null

                                MockRepository.registerUser(
                                    User(
                                        id = userId,
                                        username = username,
                                        email = email,
                                        role = selectedRole
                                    ),
                                    coachInfo = coachInfo
                                )

                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        },
                        isLoading = isLoading
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Déjà un compte ? ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Se connecter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
