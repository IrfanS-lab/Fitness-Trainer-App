package com.example.fitnesstrainerapp.screen1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.R // Import your project's R file
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly // Distributes space evenly
    ) {
        // --- Top Title ---
        Text(
            text = "FITNESS TRAINER",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        // --- Logo Image ---
        Image(
            painter = painterResource(id = R.drawable.logo_fitness), // Make sure your logo is named fitness_logo.png
            contentDescription = "Fitness Trainer Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f) // Takes up 70% of the screen width
                .aspectRatio(1f) // Ensures the image is a circle
        )

        // --- Subtitle and Quote ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "START YOUR JOURNEY STRONG",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"The only bad workout is the one that didn't happen\"",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }

        // --- Login and Signup Buttons ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Login Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(50), // Fully rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC88DFF) // Purple color
                )
            ) {
                Text(text = "LOGIN", fontWeight = FontWeight.Bold)
            }

            // Signup Button
            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(50), // Fully rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE9D0FF) // Lighter purple color
                )
            ) {
                Text(text = "SIGNUP", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    FitnessTrainerAppTheme {
        WelcomeScreen(onLoginClick = {}, onSignUpClick = {})
    }
}
