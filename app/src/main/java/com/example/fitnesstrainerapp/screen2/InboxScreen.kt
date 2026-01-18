package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

// --- Data Models for Notifications ---

sealed interface Notification {
    val id: Int
}

data class MessageNotification(
    override val id: Int,
    val trainerName: String,
    val message: String,
    val avatarRes: Int,
    val unreadCount: Int? = null
) : Notification

data class ProgressNotification(
    override val id: Int,
    val title: String,
    val progressInfo: String
) : Notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onNavigateBack: () -> Unit
) {
    // State management for notifications
    var notifications by remember {
        mutableStateOf(listOf(
            MessageNotification(1, "Trainer: Maya S", "replied to your question", R.drawable.coach1),
            MessageNotification(2, "Trainer: Maya S", "replied to your question", R.drawable.coach1),
            ProgressNotification(3, "Next progress", "50% to complete the workout"),
            ProgressNotification(4, "Next progress", "50% to complete the workout"),
            MessageNotification(5, "Trainer: Maya S", "replied to your question", R.drawable.coach3),
            MessageNotification(6, "Trainer: Alex J", "has a few messages", R.drawable.coach2),
            ProgressNotification(7, "Next progress", "100% completed"),
            MessageNotification(8, "Trainer: Alex J", "has a few messages", R.drawable.coach2, unreadCount = 3)
        ))
    }

    var selectedNotification by remember { mutableStateOf<Notification?>(null) }

    // Dialog for showing notification details
    selectedNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { selectedNotification = null },
            title = {
                Text(
                    text = when (notification) {
                        is MessageNotification -> notification.trainerName
                        is ProgressNotification -> notification.title
                    }
                )
            },
            text = {
                Text(
                    text = when (notification) {
                        is MessageNotification -> notification.message
                        is ProgressNotification -> notification.progressInfo
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedNotification = null }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inbox", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Button to add a new demo message
                    TextButton(onClick = {
                        val newId = (notifications.maxOfOrNull { it.id } ?: 0) + 1
                        notifications = listOf(
                            MessageNotification(newId, "System", "New workout tip added!", R.drawable.logo_fitness)
                        ) + notifications
                    }) {
                        Text("Add")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "ALL NOTIFICATIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(notifications, key = { it.id }) { notification ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedNotification = notification }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        NotificationItem(notification = notification)
                    }
                    IconButton(onClick = {
                        notifications = notifications.filter { it.id != notification.id }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    when (notification) {
        is MessageNotification -> MessageNotificationItem(notification = notification)
        is ProgressNotification -> ProgressNotificationItem(notification = notification)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageNotificationItem(notification: MessageNotification) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BadgedBox(
            badge = {
                if (notification.unreadCount != null) {
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text(text = notification.unreadCount.toString())
                    }
                }
            }
        ) {
            Image(
                painter = painterResource(id = notification.avatarRes),
                contentDescription = notification.trainerName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Column {
            Text(text = notification.trainerName, fontWeight = FontWeight.Bold)
            Text(text = notification.message, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProgressNotificationItem(notification: ProgressNotification) {
    Column {
        Text(text = notification.title, fontWeight = FontWeight.Bold)
        Text(text = notification.progressInfo, color = Color.Gray, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun InboxScreenPreview() {
    FitnessTrainerAppTheme {
        InboxScreen(onNavigateBack = {})
    }
}
