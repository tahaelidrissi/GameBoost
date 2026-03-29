package com.gamecoach.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTextField
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.PrimaryBlue
import com.gamecoach.ui.theme.PrimaryPurple


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo et titre
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "GameBoost",
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GameBoost",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Connectez-vous à votre compte",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Card de connexion
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
                        value = email,
                        onValueChange = { 
                            email = it
                            if (errorMessage.isNotEmpty()) errorMessage = ""
                        },
                        label = "Email",
                        leadingIcon = Icons.Default.Email
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it 
                            if (errorMessage.isNotEmpty()) errorMessage = ""
                        },
                        label = { Text("Mot de passe") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GameCoachButton(
                        text = "Se connecter",
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Veuillez remplir tous les champs"
                            } else if (!MockRepository.isUserRegistered(email)) {
                                // VÉRIFICATION BACKEND SIMULÉE
                                errorMessage = "Utilisateur non inscrit. Veuillez créer un compte."
                            } else {
                                isLoading = true
                                
                                // Redirection basée sur l'email pour la phase de test
                                val targetRoute = when {
                                    email.contains("admin", ignoreCase = true) -> Screen.AdminHome.createRoute(email)
                                    email.contains("coach", ignoreCase = true) -> Screen.CoachHome.createRoute(email)
                                    else -> Screen.PlayerHome.createRoute(email)
                                }

                                navController.navigate(targetRoute) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
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
                            text = "Pas encore de compte ? ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "S'inscrire",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Register.route)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Comptes de test
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Comptes de test :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Joueur: joueur@test.com / Player123",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Coach: coach@test.com / Coach123",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Admin: admin@test.com / Admin123",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
