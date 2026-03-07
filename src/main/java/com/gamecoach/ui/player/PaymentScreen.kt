package com.gamecoach.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    var isProcessing by remember { mutableStateOf(false) }
    var isPaid by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = if (isPaid) "Paiement Réussi" else "Paiement",
                onBackClick = { 
                    if (isPaid) {
                        navController.popBackStack()
                    } else {
                        navController.navigateUp()
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isPaid) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Récapitulatif
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Récapitulatif",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Divider(color = Color.White.copy(alpha = 0.3f))

                            PaymentRow("Session de coaching", "35.00€")
                            PaymentRow("Durée", "1 heure")
                            PaymentRow("Coach", "ProValorant")

                            Divider(color = Color.White.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "35.00€",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Simulation de paiement
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Success
                            )

                            Text(
                                text = "Paiement simulé",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Cette fonctionnalité simule un paiement. Aucune transaction réelle ne sera effectuée.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    GameCoachButton(
                        text = "Simuler le paiement",
                        onClick = {
                            isProcessing = true
                            // Simulation d'un délai de traitement
                            scope.run {
                                // On utilise SideEffect ou LaunchedEffect mais ici un simple clic suffit
                                // On va simuler le délai directement dans le onClick pour l'exemple
                            }
                            // Pour simuler proprement dans Compose sans bloquer le thread UI
                            // Normalement on utiliserait un ViewModel
                        },
                        isLoading = isProcessing
                    )

                    // Petit hack pour la simulation car on n'a pas accès au ViewModel ici
                    LaunchedEffect(isProcessing) {
                        if (isProcessing) {
                            delay(2000)
                            isProcessing = false
                            isPaid = true
                        }
                    }
                }
            } else {
                // Vue de succès
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 500),
                        label = "scale"
                    )

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale),
                        tint = Success
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Paiement Réussi !",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Votre session a été payée avec succès. Vous pouvez maintenant accéder à tous les détails de votre session.",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    GameCoachButton(
                        text = "Retour à mes sessions",
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
