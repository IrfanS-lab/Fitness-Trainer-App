package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

// A sealed interface to represent different types of notifications
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

// --- Main Screen Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onNavigateBack: () -> Unit
) {
    // This data would typically come from a ViewModel
    val newNotifications = listOf(
        MessageNotification(1, "Trainer: Maya S", "replied to your question", R.drawable.coach1),
        MessageNotification(2, "Trainer: Maya S", "replied to your question", R.drawable.coach1),
        ProgressNotification(3, "Next progress", "50% to complete the workout"),
        ProgressNotification(4, "Next progress", "50% to complete the workout")
    )
    val oldNotifications = listOf(
        MessageNotification(5, "Trainer: Maya S", "replied to your question", R.drawable.coach3),
        MessageNotification(6, "Trainer: Alex J", "has a few messages", R.drawable.coach2),
        ProgressNotification(7, "Next progress", "100% completed"),
        MessageNotification(8, "Trainer: Alex J", "has a few messages", R.drawable.coach2, unreadCount = 3),
        MessageNotification(9, "Trainer: Alex J", "has a few messages", R.drawable.coach2, unreadCount = 3),
        ProgressNotification(10, "Next progress", "100% completed")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inbox", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // New Notifications Section
            item {
                Text(
                    text = "NEW NOTIFICATIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(newNotifications, key = { "new_${it.id}" }) { notification ->
                NotificationItem(notification = notification)
            }

            // Old Notifications Section
            item {
                Text(
                    text = "OLD NOTIFICATIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(oldNotifications, key = { "old_${it.id}" }) { notification ->
                NotificationItem(notification = notification)
            }
        }
    }
}

// --- Reusable Notification Item Composable ---

@Composable
fun NotificationItem(notification: Notification) {
    // Use a 'when' statement to display the correct composable for each notification type
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        BadgedBox(
            badge = {
                if (notification.unreadCount != null) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) { Text(text = notification.unreadCount.toString()) }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Aligns with avatar-based items
    ) {
        Text(text = notification.title, fontWeight = FontWeight.Bold)
        Text(text = notification.progressInfo, color = Color.Gray, fontSize = 14.sp)
    }
}

// --- Preview ---

@Preview(showBackground = true, widthDp = 360)
@Composable
fun InboxScreenPreview() {
    FitnessTrainerAppTheme {
        InboxScreen(onNavigateBack = {})
    }
}
