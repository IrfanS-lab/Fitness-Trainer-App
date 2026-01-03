package com.example.fitnesstrainerapp.screen2

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

// Data class to represent a workout progress item
data class WorkoutProgress(
    val id: Int,
    val name: String,
    val progress: Float, // A value between 0.0f and 1.0f
    val imageRes: Int // The resource ID from R.drawable (can be a GIF)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    onNavigateBack: () -> Unit
) {
    // --- State Management for Editable Username ---
    // The user's name is now a state that can change.
    var userName by remember {
        mutableStateOf(userEmail.substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
    }
    // A flag to control when the edit dialog is shown.
    var showEditDialog by remember { mutableStateOf(false) }

    // This data would typically come from a ViewModel
    val workoutHistory = listOf(
        WorkoutProgress(1, "PushUp", 0.7f, R.drawable.workout_pushup),
        WorkoutProgress(2, "Squats", 0.9f, R.drawable.workout_squat),
        WorkoutProgress(3, "Side Squats", 0.5f, R.drawable.workout_side_squat)
    )

    // --- Show the Edit Dialog when showEditDialog is true ---
    if (showEditDialog) {
        EditUsernameDialog(
            currentName = userName,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                userName = newName
                showEditDialog = false
                // TODO: In a real app, you would save this newName to your database/server here.
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // FIX 3: User Info Header now uses the dynamic data and triggers the dialog
            item {
                UserInfoHeader(
                    name = userName,
                    email = userEmail,
                    onEditClick = {
                        showEditDialog = true // Set the flag to show the dialog
                    }
                )
            }

            // 2. Workout Progress List
            items(workoutHistory, key = { it.id }) { workout ->
                WorkoutProgressCard(workout = workout)
            }
        }
    }
}

/**
 * A dialog box for editing the username.
 */
@Composable
fun EditUsernameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Username") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newName) },
                // The button is only enabled if the new name is not blank.
                enabled = newName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


/**
 * Displays the user's avatar, name, and email.
 */
@Composable
fun UserInfoHeader(name: String, email: String, onEditClick:() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // User Avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8E0FF)), // Light purple background
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                tint = Color(0xFF7B4DFF), // Purple icon color
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // User Name and Email
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        //EDIT BUTTON
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Username",
                tint = Color.Gray
            )
        }
    }
}

/**
 * Displays a single workout progress item with a GIF/Image and progress bar.
 */
@Composable
fun WorkoutProgressCard(workout: WorkoutProgress) {
    // This custom ImageLoader is essential for playing GIFs.
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // AsyncImage from Coil handles loading GIFs and other image types automatically.
        AsyncImage(
            model = workout.imageRes,
            contentDescription = workout.name,
            imageLoader = imageLoader, // Provide the custom image loader
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))

        // --- UPGRADED PROGRESS DISPLAY ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp) // Increase height to make text more readable
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.CenterStart
        ) {
            // The progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(workout.progress) // The width is determined by the progress
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            // The percentage text, centered in the whole box
            Text(
                text = "${(workout.progress * 100).toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FitnessTrainerAppTheme {
        // FIX 4: Update the preview to reflect the new function signature
        ProfileScreen(
            userEmail = "preview@example.com",
            onNavigateBack = {}
        )
    }
}
