package com.gamecoach.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.gamecoach.model.User
import com.gamecoach.model.UserRole
import com.gamecoach.ui.components.EmptyState
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingCoachesScreen(navController: NavController, email: String = "admin@test.com") {
    // Utilisation de snapshots réactifs pour éviter les crashs et assurer la mise à jour
    val pendingUsers = MockRepository.registeredUsers.filter { it.status == com.gamecoach.model.UserStatus.PENDING }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Validations en attente",
                onBackClick = { navController.navigateUp() },
                onHomeClick = {
                    navController.navigate(Screen.AdminHome.createRoute(email)) {
                        popUpTo(Screen.AdminHome.route) { inclusive = true }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, null, tint = Warning)
                    Text("${pendingUsers.size} inscription(s) à valider", fontWeight = FontWeight.Bold)
                }
            }

            if (pendingUsers.isEmpty()) {
                EmptyState(icon = Icons.Default.CheckCircle, message = "Toutes les inscriptions sont validées")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingUsers, key = { it.email }) { user ->
                        PendingUserCard(
                            user = user,
                            onApprove = {
                                MockRepository.approveUser(user.email)
                            },
                            onReject = {
                                MockRepository.rejectUser(user.email)
                            },
                            onClick = {
                                if (user.role == UserRole.COACH) {
                                    val coach = MockRepository.coaches.find { it.id == user.id || it.email == user.email }
                                    if (coach != null) {
                                        navController.navigate(Screen.CoachDetailAdmin.createRoute(coach.id))
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingUserCard(
    user: User,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    color = if (user.role == UserRole.COACH) PrimaryPurple.copy(alpha = 0.1f) else PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(user.username.take(1).uppercase(), fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(user.username, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(
                        text = if (user.role == UserRole.COACH) "Rôle: Coach" else "Rôle: Joueur",
                        color = if (user.role == UserRole.COACH) PrimaryPurple else PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                
                if (user.role == UserRole.COACH) {
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Text("Rejeter")
                }
                
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Success)
                ) {
                    Text("Approuver")
                }
            }
        }
    }
}
