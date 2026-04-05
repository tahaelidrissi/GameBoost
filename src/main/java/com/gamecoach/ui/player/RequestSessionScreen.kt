package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Session
import com.gamecoach.model.SessionStatus
import com.gamecoach.ui.components.GameCoachButton
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestSessionScreen(navController: NavController, coachId: String = "1", email: String = "") {
    val coach = remember(coachId) { MockRepository.getCoachById(coachId) }
    val user = remember(email) { MockRepository.getUserByEmail(email) }
    
    var selectedDuration by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    val hourlyRate = coach?.hourlyRate ?: 35.0
    val totalAmount = hourlyRate * selectedDuration

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Demander une session",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    navController.navigate(Screen.PlayerHome.createRoute(email)) {
                        popUpTo(Screen.PlayerHome.route) { inclusive = true }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Coach Info Summary
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f))) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text("Coach: ${coach?.username ?: "Expert"}", fontWeight = FontWeight.Bold)
                        Text("Jeu: ${coach?.game ?: "Multijeux"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Durée
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Durée de la session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 4, 5).forEach { duration ->
                            FilterChip(
                                selected = selectedDuration == duration,
                                onClick = { selectedDuration = duration },
                                label = { Text("$duration h") }
                            )
                        }
                    }
                }
            }

            // Date
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Date souhaitée",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedDate.isEmpty()) "Sélectionner une date" else selectedDate)
                    }
                }
            }

            // Heure
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Heure de début",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedTime.isEmpty()) "Sélectionner une heure" else selectedTime)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Total et Confirmation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", fontWeight = FontWeight.Bold)
                        Text(
                            "${totalAmount}€",
                            fontWeight = FontWeight.Bold,
                            color = Success
                        )
                    }
                    GameCoachButton(
                        text = "Confirmer la demande",
                        enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty(),
                        onClick = {
                            // FRONTEND RESPONSIBILITY: Créer l'objet session et l'envoyer au "backend"
                            val newSession = Session(
                                id = UUID.randomUUID().toString(),
                                playerId = user?.id ?: "0",
                                playerName = user?.username ?: "Joueur Anonyme",
                                coachId = coachId,
                                coachName = coach?.username ?: "Expert",
                                game = coach?.game ?: "Multijeux",
                                duration = selectedDuration,
                                scheduledDate = selectedDate,
                                scheduledTime = selectedTime,
                                amount = totalAmount,
                                status = SessionStatus.PENDING,
                                isPaid = false
                            )
                            MockRepository.addSession(newSession)
                            navController.navigate(Screen.PlayerSessions.createRoute(email)) {
                                popUpTo(Screen.PlayerHome.route)
                            }
                        }
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Date(it)
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        selectedDate = formatter.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
