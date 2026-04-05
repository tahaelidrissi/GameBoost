package com.gamecoach.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController, email: String = "admin@test.com") {
    var selectedFilter by remember { mutableStateOf<UserRole?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Utilisation des données réelles du MockRepository
    val users = MockRepository.registeredUsers

    val filteredUsers = users.filter { user ->
        (selectedFilter == null || user.role == selectedFilter) &&
                (searchQuery.isEmpty() || user.username.contains(searchQuery, ignoreCase = true) ||
                        user.email.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Utilisateurs",
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
            // Recherche et filtres
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rechercher par nom ou email...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("Tous") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedFilter == UserRole.PLAYER,
                        onClick = { selectedFilter = UserRole.PLAYER },
                        label = { Text("Joueurs") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedFilter == UserRole.COACH,
                        onClick = { selectedFilter = UserRole.COACH },
                        label = { Text("Coachs") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                "${filteredUsers.size} utilisateur(s) trouvé(s)",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (filteredUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun utilisateur ne correspond à votre recherche", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers, key = { it.email }) { user ->
                        UserCard(user = user)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = when (user.role) {
                    UserRole.PLAYER -> PrimaryBlue.copy(alpha = 0.1f)
                    UserRole.COACH -> Success.copy(alpha = 0.1f)
                    UserRole.ADMIN -> Error.copy(alpha = 0.1f)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (user.role) {
                            UserRole.PLAYER -> Icons.Default.Person
                            UserRole.COACH -> Icons.Default.School
                            UserRole.ADMIN -> Icons.Default.Shield
                        },
                        contentDescription = null,
                        tint = when (user.role) {
                            UserRole.PLAYER -> PrimaryBlue
                            UserRole.COACH -> Success
                            UserRole.ADMIN -> Error
                        }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.username,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.email,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    "Inscrit le ${user.createdAt}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Surface(
                color = when (user.role) {
                    UserRole.PLAYER -> PrimaryBlue.copy(alpha = 0.1f)
                    UserRole.COACH -> Success.copy(alpha = 0.1f)
                    UserRole.ADMIN -> Error.copy(alpha = 0.1f)
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    when (user.role) {
                        UserRole.PLAYER -> "Joueur"
                        UserRole.COACH -> "Coach"
                        UserRole.ADMIN -> "Admin"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (user.role) {
                        UserRole.PLAYER -> PrimaryBlue
                        UserRole.COACH -> Success
                        UserRole.ADMIN -> Error
                    }
                )
            }
        }
    }
}
