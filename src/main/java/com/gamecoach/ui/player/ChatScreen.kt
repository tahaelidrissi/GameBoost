package com.gamecoach.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gamecoach.data.MockRepository
import com.gamecoach.model.Message
import com.gamecoach.ui.components.GameCoachTopBar
import com.gamecoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, sessionId: String = "", currentUserEmail: String = "") {
    var messageText by remember { mutableStateOf("") }
    
    // Observation directe des données du repository pour la réactivité
    val session = remember(sessionId) { MockRepository.getSessionById(sessionId) }
    val currentUser = remember(currentUserEmail, MockRepository.registeredUsers) { 
        MockRepository.getUserByEmail(currentUserEmail) 
    }
    
    val messages by remember(sessionId) {
        derivedStateOf { 
            MockRepository.messages.filter { it.sessionId == sessionId }
                .sortedBy { it.timestamp } // Optionnel: trier par temps
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll vers le bas quand un nouveau message arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            GameCoachTopBar(
                title = if (currentUser?.role == com.gamecoach.model.UserRole.COACH) 
                    session?.playerName ?: "Joueur" 
                else 
                    session?.coachName ?: "Coach",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Écrivez votre message...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && currentUser != null) {
                                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val newMessage = Message(
                                    id = UUID.randomUUID().toString(),
                                    sessionId = sessionId,
                                    senderId = currentUser.id,
                                    senderName = currentUser.username,
                                    content = messageText.trim(),
                                    timestamp = sdf.format(Date())
                                )
                                MockRepository.sendMessage(newMessage)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank() && currentUser != null
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Envoyer",
                            tint = if (messageText.isNotBlank()) PrimaryBlue else Color.Gray
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aucun message. Commencez la discussion !", color = Color.Gray)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUser?.id
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) PrimaryBlue else Color(0xFFE5E7EB),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!isMe) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryPurple,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Text(
                    text = message.content,
                    color = if (isMe) Color.White else TextPrimary
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
