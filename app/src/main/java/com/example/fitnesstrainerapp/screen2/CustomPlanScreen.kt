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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.data.AppDatabase
import com.example.fitnesstrainerapp.data.WorkoutEntity
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import kotlinx.coroutines.launch

// --- Data Model for a Custom Plan Exercise ---
data class CustomExercise(
    val id: Int,
    val title: String,
    @DrawableRes val imageRes: Int,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPlanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val exercises = listOf(
        CustomExercise(1, "10 PUSH UP", R.drawable.workout_pushup, 0.7f),
        CustomExercise(2, "10 SQUATS", R.drawable.workout_squat, 0.9f),
        CustomExercise(3, "15 SIDE SQUATS", R.drawable.workout_side_squat, 0.5f),
        CustomExercise(4, "20 LUNGES", R.drawable.workout_3, 0.3f),
        CustomExercise(5, "20 PUSH UP", R.drawable.workout_pushup, 0.1f),
        CustomExercise(6, "25 SQUATS", R.drawable.workout_squat, 0.2f),
        CustomExercise(7, "10 MIN JUMPING JACKS", R.drawable.workout_3, 0.0f)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Custom plan", fontWeight = FontWeight.Bold) },
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
            item { CustomPlanBanner() }

            item {
                Text(
                    text = "OPTION PLAN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(exercises, key = { it.id }) { exercise ->
                CustomExerciseCard(
                    item = exercise,
                    onStartClick = {
                        scope.launch {
                            db.workoutDao().insertWorkout(
                                WorkoutEntity(
                                    name = exercise.title,
                                    progress = exercise.progress,
                                    imageRes = exercise.imageRes
                                )
                            )
                            onNavigateToProfile()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomExerciseCard(
    item: CustomExercise,
    onStartClick: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        AsyncImage(
            model = item.imageRes,
            contentDescription = item.title,
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onStartClick,
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

            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray
            )
        }
    }
}

@Composable
fun CustomPlanBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.custom_plan_banner),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)))
            Text(
                text = "Start your\nown plan",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomPlanScreenPreview() {
    FitnessTrainerAppTheme {
        CustomPlanScreen(onNavigateBack = {}, onNavigateToProfile = {})
    }
}
