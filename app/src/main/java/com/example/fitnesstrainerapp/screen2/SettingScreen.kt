package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userEmail: String,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit
) {
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
                .padding(16.dp)
        ) {
            // --- Account Settings Section ---
            Text(
                text = "Settings for: $userEmail",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SettingsItem(
                icon = Icons.Default.AccountCircle,
                title = "Profile Information",
                onClick = { /* Handle Profile Click */ }
            )
            Divider()
            SettingsItem(
                icon = Icons.Default.AdminPanelSettings,
                title = "Email & Password",
                subtitle = userEmail, // Pass the email to be displayed as a subtitle
                onClick = { /* Handle Email/Password Click */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- App Preferences Section ---
            Text(
                text = "APP PREFERENCES",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications preferences",
                onClick = { /* Handle Notifications Click */ }
            )
            Divider()
            SettingsItem(
                icon = Icons.Default.VolumeUp,
                title = "Sounds and Haptic",
                onClick = { /* Handle Sounds Click */ }
            )
            Divider()
            SettingsItem(
                icon = Icons.Default.HelpOutline,
                title = "Help Center",
                onClick = { /* Handle Help Click */ }
            )
            Divider()
            SettingsItem(
                icon = Icons.Default.MailOutline,
                title = "Terms & Services",
                onClick = { /* Handle Terms Click */ }
            )

            // Spacer to push the logout button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // --- Log Out Button ---
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Log Out",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "LOG OUT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * A reusable composable for a single row in the settings list.
 */
@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        // FIX 1: The Column now correctly contains both the title and subtitle Text composables.
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            // If a subtitle is provided, display it below the title.
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                // FIX 2: Corrected the misplaced parenthesis. The color is now inside the Text call.
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FitnessTrainerAppTheme {
        // FIX 3: Add the missing 'userEmail' parameter to the preview call.
        SettingsScreen(
            userEmail = "preview@example.com",
            onNavigateBack = {},
            onLogoutClick = {}
        )
    }
}
