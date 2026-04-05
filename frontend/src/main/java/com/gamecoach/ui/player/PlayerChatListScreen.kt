package com.gamecoach.ui.player

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.ui.components.EmptyState
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.navigation.Screen
import com.gamecoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerChatListScreen(navController: NavController, email: String = "") {
    // Utilisation des vraies données du repository
    val chats = remember(email, MockRepository.sessions, MockRepository.messages) {
        val userSessions = MockRepository.getSessionsForUser(email)
        userSessions.map { session ->
            val lastMessage = MockRepository.messages
                .filter { it.sessionId == session.id }
                .lastOrNull()
            
            ChatSummary(
                id = session.id,
                coachName = session.coachName,
                game = session.game,
                lastMessage = lastMessage?.content ?: "Prêt pour votre séance ?",
                time = lastMessage?.timestamp ?: ""
            )
        }
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = "Mes Messages"
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PlayerHome.createRoute(email)) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.CoachList.createRoute(email)) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Coachs") },
                    label = { Text("Coachs") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PlayerSessions.createRoute(email)) },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Sessions") },
                    label = { Text("Sessions") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.createRoute(email)) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { padding ->
        if (chats.isEmpty()) {
            EmptyState(icon = Icons.Default.Chat, message = "Aucun message")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(chats) { chat ->
                    ChatListItem(chat) {
                        navController.navigate(Screen.Chat.createRoute(chat.id, email))
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatSummary, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = PrimaryBlue
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = chat.coachName.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(chat.coachName, fontWeight = FontWeight.Bold)
                    Text(chat.time, fontSize = 12.sp, color = TextSecondary)
                }
                Text(
                    text = chat.lastMessage,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Text(chat.game, fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}

data class ChatSummary(
    val id: String,
    val coachName: String,
    val game: String,
    val lastMessage: String,
    val time: String
)
