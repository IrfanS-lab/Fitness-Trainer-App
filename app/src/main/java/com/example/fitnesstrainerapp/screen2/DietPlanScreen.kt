package com.example.fitnesstrainerapp.screen2

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanScreen(
    onNavigateBack: () -> Unit,
    onCaloriesCalculatorClick: () -> Unit
) {
    // 1. Get the UriHandler to open links
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Diet plan", fontWeight = FontWeight.Bold) },
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
            // Main banner card
            item {
                DietCard(
                    imageRes = R.drawable.diet_banner,
                    title = "Small Choices,\nBig Changes"
                )
            }

            // Calories Calculator Button
            item {
                Button(
                    onClick = onCaloriesCalculatorClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "CALORIES\nCALCULATOR",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // Daily Tips Section
            item {
                Text(
                    text = "DAILY TIPS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Daily Tip Cards
            item {
                DietCard(
                    imageRes = R.drawable.daily_tip_1,
                    title = "30 DAYS HEALTHY\nLIFESTYLE\nEATING PLAN",
                    onClick = {
                        uriHandler.openUri("https://www.mountsinai.org/files/MSHealth/Assets/HS/MonthlyMealPlan_Brochure_Rev24.pdf")
                    }
                )
            }

            // 2. Updated Card with the URL Link
            item {
                DietCard(
                    imageRes = R.drawable.daily_tip_2,
                    title = "NO SUGAR\n14 DAY CHALLENGE",
                    onClick = {
                        uriHandler.openUri("https://www.plateandcanvas.com/14-day-no-sugar-diet-with-food-list-a-dietitian-s-guide")
                    }
                )
            }
        }
    }
}

/**
 * A reusable composable for the image cards with text overlay.
 */
@Composable
fun DietCard(
    @DrawableRes imageRes: Int,
    title: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick), // This makes the card clickable
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DietPlanScreenPreview() {
    FitnessTrainerAppTheme {
        DietPlanScreen(onNavigateBack = {}, onCaloriesCalculatorClick = {})
    }
}