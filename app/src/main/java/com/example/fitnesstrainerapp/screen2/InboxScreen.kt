package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import com.google.firebase.firestore.DocumentId

// Data class for a single notification (Firestore compatible)
data class Notification(
    @DocumentId val id: String? = null, // Firestore document ID
    val senderName: String = "",
    val senderImageUrl: String = "",
    val message: String = "",
    val timestamp: Long = 0
)

// Data class to hold the grouped information for the UI
data class GroupedNotification(
    val senderName: String,
    val senderImageUrl: String,
    val messages: List<Notification> // Changed to hold all messages
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    notifications: List<Notification>,
    onNavigateBack: () -> Unit,
    onDeleteItemClick: (String) -> Unit, // Callback for deleting a conversation
    onMessageSend: (String, String) -> Unit, // Callback for sending a message
    trainers: List<String> // List of available trainers
) {
    var showAddMessageDialog by remember { mutableStateOf(false) }
    var selectedConversation by remember { mutableStateOf<GroupedNotification?>(null) }

    // Group notifications by senderName and transform them for the UI
    val groupedNotifications = notifications
        .groupBy { it.senderName }
        .map { (name, messages) ->
            GroupedNotification(
                senderName = name,
                senderImageUrl = messages.first().senderImageUrl,
                messages = messages.sortedBy { it.timestamp } // Sort messages by time
            )
        }

    if (showAddMessageDialog) {
        AddMessageDialog(
            trainers = trainers,
            onDismiss = { showAddMessageDialog = false },
            onSend = {
                recipient, message -> onMessageSend(recipient, message)
                showAddMessageDialog = false
            }
        )
    }

    selectedConversation?.let {
        ConversationDialog(
            conversation = it,
            onDismiss = { selectedConversation = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showAddMessageDialog = true }) {
                        Text("Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "ALL NOTIFICATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Gray
                )
            }

            items(groupedNotifications, key = { it.senderName }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { selectedConversation = notification },
                    onDelete = { onDeleteItemClick(notification.senderName) }
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: GroupedNotification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgedBox(
            badge = {
                if (notification.messages.isNotEmpty()) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(text = notification.messages.size.toString())
                    }
                }
            }
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = notification.senderImageUrl),
                contentDescription = "${notification.senderName} profile picture",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.senderName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.messages.lastOrNull()?.message ?: "",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete conversation",
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMessageDialog(
    trainers: List<String>,
    onDismiss: () -> Unit,
    onSend: (String, String) -> Unit
) {
    var selectedTrainer by remember { mutableStateOf(trainers.firstOrNull() ?: "") }
    var message by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Send a New Message") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTrainer,
                        onValueChange = {},
                        label = { Text("Trainer") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        trainers.forEach { trainer ->
                            DropdownMenuItem(
                                text = { Text(trainer) },
                                onClick = {
                                    selectedTrainer = trainer
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(selectedTrainer, message) },
                enabled = selectedTrainer.isNotBlank() && message.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConversationDialog(
    conversation: GroupedNotification,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chat with ${conversation.senderName}") },
        text = {
            LazyColumn {
                items(conversation.messages) { message ->
                    Text(message.message, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun InboxScreenPreview() {
    val sampleNotifications = listOf(
        Notification("1", "David", "url_to_david_image", "hiii", 1L),
        Notification("2", "David", "url_to_david_image", "i need tips to build up muscle", 2L),
        Notification("3", "Sarah", "url_to_sarah_image", "how to know the suitable diet for us?", 3L),
        Notification("4", "Emily", "url_to_emily_image", "i need diet tips", 4L)
    )
    val sampleTrainers = listOf("David", "Sarah", "Emily", "John")

    FitnessTrainerAppTheme {
        InboxScreen(
            notifications = sampleNotifications,
            onNavigateBack = { },
            onDeleteItemClick = { },
            onMessageSend = { _, _ -> },
            trainers = sampleTrainers
        )
    }
}
