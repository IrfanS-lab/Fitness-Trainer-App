package com.example.fitnesstrainerapp.screen2

import android.util.Log
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// --- Updated Data Models for Firestore ---

sealed interface Notification {
    val id: String // Changed to String for Firestore document IDs
}

data class MessageNotification(
    override val id: String = "",
    val senderName: String = "",
    val message: String = "",
    val avatarRes: Int = R.drawable.coach1,
    val unreadCount: Int? = null
) : Notification {
    // Helper function to convert Firestore map to object
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): MessageNotification {
            return MessageNotification(
                id = id,
                senderName = map["senderName"] as? String ?: "",
                message = map["message"] as? String ?: "",
                avatarRes = (map["avatarRes"] as? Long)?.toInt() ?: R.drawable.coach1,
                unreadCount = (map["unreadCount"] as? Long)?.toInt()
            )
        }
    }
}

data class ProgressNotification(
    override val id: String = "",
    val title: String = "",
    val progressInfo: String = ""
) : Notification {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): ProgressNotification {
            return ProgressNotification(
                id = id,
                title = map["title"] as? String ?: "",
                progressInfo = map["progressInfo"] as? String ?: ""
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onNavigateBack: () -> Unit
) {
    val db = Firebase.firestore
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch notifications from Firestore
    LaunchedEffect(Unit) {
        db.collection("notifications")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedNotifications = snapshot.documents.map { doc ->
                        val data = doc.data ?: emptyMap()
                        val type = data["type"] as? String
                        if (type == "progress") {
                            ProgressNotification.fromMap(doc.id, data)
                        } else {
                            MessageNotification.fromMap(doc.id, data)
                        }
                    }
                    notifications = fetchedNotifications
                    isLoading = false
                }
            }
    }

    var selectedNotification by remember { mutableStateOf<Notification?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog view logic
    selectedNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { selectedNotification = null },
            title = {
                Text(
                    text = when (notification) {
                        is MessageNotification -> notification.senderName
                        is ProgressNotification -> notification.title
                    },
                    fontWeight = FontWeight.Bold
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
                    Text("Close")
                }
            }
        )
    }

    if (showAddDialog) {
        AddMessageDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, content ->
                val newNotification = hashMapOf(
                    "senderName" to name,
                    "message" to content,
                    "avatarRes" to R.drawable.coach1,
                    "unreadCount" to 1,
                    "type" to "message",
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
                db.collection("notifications").add(newNotification)
                showAddDialog = false
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
                    TextButton(onClick = { showAddDialog = true }) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                            db.collection("notifications").document(notification.id).delete()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun AddMessageDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var senderName by remember { mutableStateOf("") }
    var messageContent by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Message", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = senderName,
                    onValueChange = { senderName = it },
                    label = { Text("Sender name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = messageContent,
                    onValueChange = { messageContent = it },
                    label = { Text("Type a message") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(senderName, messageContent) },
                enabled = senderName.isNotBlank() && messageContent.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
                contentDescription = notification.senderName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Column {
            Text(text = notification.senderName, fontWeight = FontWeight.Bold)
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
