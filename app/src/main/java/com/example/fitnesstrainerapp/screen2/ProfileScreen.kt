package com.example.fitnesstrainerapp.screen2

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.fitnesstrainerapp.data.AppDatabase
import com.example.fitnesstrainerapp.data.WorkoutEntity
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    // Add Firebase instances
    val auth = Firebase.auth
    val firestore = Firebase.firestore

    // Observe workout history from the database (Flow automatically updates UI)
    val workoutHistory by db.workoutDao().getAllWorkouts().collectAsState(initial = emptyList())

    var userName by remember {
        mutableStateOf(userEmail.substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
    }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditUsernameDialog(
            currentName = userName,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                userName = newName
                showEditDialog = false
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
            item {
                UserInfoHeader(
                    name = userName,
                    email = userEmail,
                    onEditClick = { showEditDialog = true }
                )
            }

            if (workoutHistory.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No workout history yet.", color = Color.Gray)
                    }
                }
            } else {
                items(workoutHistory, key = { it.id }) { workout ->
                    WorkoutProgressCard(
                        workout = workout,
                        onDelete = {
                            scope.launch {
                                db.workoutDao().deleteWorkout(workout)
                            }
                        },
                        onUpdate = {
                            scope.launch {
                                // 1. Update local Room database
                                val newProgress = min(workout.progress + 0.1f, 1.0f)
                                val updatedWorkout = workout.copy(progress = newProgress)
                                db.workoutDao().updateWorkout(updatedWorkout)

                                // 2. Save the date to Firebase
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    saveDateToFirebase(firestore, userId)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// NEW: Function to save the workout date to Firestore
private suspend fun saveDateToFirebase(firestore: com.google.firebase.firestore.FirebaseFirestore, userId: String) {
    // Use a simple date format as the document ID (e.g., "2026-01-19")
    // This prevents duplicate entries for the same day.
    val dateId = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val workoutDate = mapOf(
        "userId" to userId,
        "completedAt" to FieldValue.serverTimestamp() // Store the precise time
    )

    // Using .set() on a document with a specific ID will create it or overwrite it.
    // This is ideal for ensuring only one entry per user per day.
    try {
        firestore.collection("workout_dates")
            .document("$userId-$dateId") // Unique ID per user per day
            .set(workoutDate)
            .await()
    } catch (e: Exception) {
        // Handle potential exceptions, e.g., network issues
        e.printStackTrace()
    }
}


@Composable
fun EditUsernameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
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
            Button(onClick = { onConfirm(newName) }, enabled = newName.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UserInfoHeader(name: String, email: String, onEditClick:() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFE8E0FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "User Avatar", tint = Color(0xFF7B4DFF), modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Username", tint = Color.Gray)
        }
    }
}

// UPDATED: Added onUpdate lambda
@Composable
fun WorkoutProgressCard(workout: WorkoutEntity, onDelete: () -> Unit, onUpdate: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }.build()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Workout", tint = Color.LightGray)
            }
        }

        AsyncImage(
            model = workout.imageRes,
            contentDescription = workout.name,
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        Box(
            modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(workout.progress)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "${(workout.progress * 100).toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // NEW: Update button
        Button(
            onClick = onUpdate,
            modifier = Modifier.fillMaxWidth(),
            // Disable the button if progress is already 100%
            enabled = workout.progress < 1.0f
        ) {
            Text("UPDATE PROGRESS (+10%)")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FitnessTrainerAppTheme {
        ProfileScreen(userEmail = "preview@example.com", onNavigateBack = {})
    }
}

