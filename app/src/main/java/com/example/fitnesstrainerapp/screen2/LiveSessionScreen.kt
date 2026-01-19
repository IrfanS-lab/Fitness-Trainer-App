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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.example.fitnesstrainerapp.R

// --- Data Model ---
data class LiveSessionItem(
    val id: Int,
    @field:DrawableRes val imageRes: Int,
    val youtubeUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionScreen(
    onNavigateBack: () -> Unit
) {
    // Updated list of live sessions with specific YouTube URLs for each workout
    val liveSessions = listOf(
        LiveSessionItem(
            id = 1,
            imageRes = R.drawable.workout_squat,
            youtubeUrl = "https://www.youtube.com/watch?v=aclHkVaku9U" // Squat tutorial
        ),
        LiveSessionItem(
            id = 2,
            imageRes = R.drawable.workout_pushup,
            youtubeUrl = "https://www.youtube.com/watch?v=IODxDxX7oi4" // Pushup tutorial
        ),
        LiveSessionItem(
            id = 3,
            imageRes = R.drawable.workout_side_squat,
            youtubeUrl = "https://youtu.be/Pe115ryKDwQ?si=qPnkAq8y2V07rUtK" // Side Squat tutorial
        ),
        LiveSessionItem(
            id = 4,
            imageRes = R.drawable.workout_3,
            youtubeUrl = "https://youtu.be/5yrRws8Du1k?si=Xyr2mAnfNaMGU1qv" // Another workout tutorial
        )
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
            item { LiveSessionBanner() }

            item {
                Text(
                    text = "LIVE SESSION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(liveSessions, key = { it.id }) { sessionItem ->
                LiveWorkoutCard(item = sessionItem)
            }
        }
    }
}

@Composable
fun LiveWorkoutCard(item: LiveSessionItem) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            }
            // Note: For older APIs, you might need a different decoder like coil-gif
            // if you are loading GIFs, but for static drawables this is generally fine.
        }
        .build()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = item.imageRes,
            contentDescription = "Live Workout",
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        Button(
            onClick = {
                uriHandler.openUri(item.youtubeUrl)
            },
            modifier = Modifier.align(Alignment.Start),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEDE7F6),
                contentColor = Color(0xFF673AB7)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("START", fontWeight = FontWeight.Bold)
        }
    }
}

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
                painter = painterResource(id = R.drawable.custom_plan_banner),
                contentDescription = "Start your live session",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            Text(
                text = "START YOUR LIVE SESSION",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}
