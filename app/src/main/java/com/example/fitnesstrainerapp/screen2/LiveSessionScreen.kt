package com.example.fitnesstrainerapp.screen2

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

// --- Data Model for a Live Session Item ---
data class LiveSessionItem(
    val id: Int,
    @DrawableRes val imageRes: Int // Can be a GIF or PNG
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionScreen(
    onNavigateBack: () -> Unit
) {
    // This data would typically come from a ViewModel or API
    val liveSessions = listOf(
        LiveSessionItem(1, R.drawable.workout_squat),
        LiveSessionItem(2, R.drawable.workout_pushup)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Live session", fontWeight = FontWeight.Bold) },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Top Banner Card
            item {
                LiveSessionBanner()
            }

            // 2. "LIVE SESSION" Header
            item {
                Text(
                    text = "LIVE SESSION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 3. List of Live Session Workouts
            items(liveSessions, key = { it.id }) { sessionItem ->
                LiveWorkoutCard(item = sessionItem)
            }
        }
    }
}

// --- Reusable Sub-Components ---

@Composable
fun LiveSessionBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Image(
                painter = painterResource(id = R.drawable.custom_plan_banner), // Your banner image
                contentDescription = "Start your live session",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Scrim for better text readability
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            Text(
                text = "START YOUR\nLIVE SESSION",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun LiveWorkoutCard(item: LiveSessionItem) {
    // Custom ImageLoader is essential for playing GIFs.
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // The GIF/Image display
        AsyncImage(
            model = item.imageRes,
            contentDescription = "Live Workout",
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )
        // The "Start" button
        Button(
            onClick = { /* TODO: Handle Start button click */ },
            modifier = Modifier.align(Alignment.Start),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEDE7F6), // Light purple
                contentColor = Color(0xFF673AB7)    // Darker purple
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("START", fontWeight = FontWeight.Bold)
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun LiveSessionScreenPreview() {
    FitnessTrainerAppTheme {
        LiveSessionScreen(onNavigateBack = {})
    }
}
