package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Enum to manage which dialog is visible
enum class SettingDialogType {
    NONE, NOTIFICATIONS, SOUND_HAPTIC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userEmail: String,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // State to track which dialog is open
    var activeDialog by remember { mutableStateOf(SettingDialogType.NONE) }

    // Logic to show the correct Dialog based on the image
    when (activeDialog) {
        SettingDialogType.NOTIFICATIONS -> {
            NotificationPreferencesDialog(
                onDismiss = { activeDialog = SettingDialogType.NONE },
                onSave = { activeDialog = SettingDialogType.NONE }
            )
        }
        SettingDialogType.SOUND_HAPTIC -> {
            SoundHapticDialog(
                onDismiss = { activeDialog = SettingDialogType.NONE },
                onSave = { activeDialog = SettingDialogType.NONE }
            )
        }
        SettingDialogType.NONE -> {}
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "APP PREFERENCES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Notifications preferences
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications preferences",
                onClick = { activeDialog = SettingDialogType.NOTIFICATIONS }
            )

            // 2. Sounds and Haptic
            SettingsItem(
                icon = Icons.Default.VolumeUp,
                title = "Sounds and Haptic",
                onClick = { activeDialog = SettingDialogType.SOUND_HAPTIC }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button - Matches the light grey rectangle in the image
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0),
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG OUT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Popup 1: Notifications ---
@Composable
fun NotificationPreferencesDialog(onDismiss: () -> Unit, onSave: () -> Unit) {
    var pushEnabled by remember { mutableStateOf(false) }
    var emailEnabled by remember { mutableStateOf(true) }
    var inAppEnabled by remember { mutableStateOf(true) }

    BaseSettingDialog(
        title = "Notifications Preferences",
        onDismiss = onDismiss,
        onSave = onSave
    ) {
        DialogSwitchRow("Push Notifications", pushEnabled) { pushEnabled = it }
        DialogSwitchRow("Email Notifications", emailEnabled) { emailEnabled = it }
        DialogSwitchRow("In-App Alerts", inAppEnabled) { inAppEnabled = it }
    }
}

// --- Popup 2: Sound & Haptic ---
@Composable
fun SoundHapticDialog(onDismiss: () -> Unit, onSave: () -> Unit) {
    var systemSounds by remember { mutableStateOf(false) }
    var workoutAlerts by remember { mutableStateOf(true) }
    var messagingTones by remember { mutableStateOf(true) }

    BaseSettingDialog(
        title = "Sound and Haptic",
        onDismiss = onDismiss,
        onSave = onSave
    ) {
        DialogSwitchRow("System sounds", systemSounds) { systemSounds = it }
        DialogSwitchRow("Workout Alerts", workoutAlerts) { workoutAlerts = it }
        DialogSwitchRow("Messaging Tones", messagingTones) { messagingTones = it }
    }
}

// Reusable Dialog Template to ensure consistency with the image
@Composable
fun BaseSettingDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))

                content()

                Spacer(modifier = Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) { Text("SAVE") }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1D1D1))
                    ) { Text("CANCEL", color = Color.Black) }
                }
            }
        }
    }
}

@Composable
fun DialogSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Black)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50) // Green color from the image
            )
        )
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Black)
        Column {
            Text(text = title, color = Color(0xFF9E9E9E), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            // The thin underline from the image
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEEEEEE)))
        }
    }
}