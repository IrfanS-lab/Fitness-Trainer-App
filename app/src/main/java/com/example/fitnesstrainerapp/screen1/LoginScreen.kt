package com.example.fitnesstrainerapp.screen1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

@Composable
fun LoginScreen(
    emailState: String,
    passwordState: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    isLoginButtonEnabled: Boolean // Renamed for clarity
) {
    // FIX 1: REMOVED the local state. This composable is now stateless.
    // var email by remember { mutableStateOf("") }
    // var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()), // Added for smaller screens
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "FITNESS TRAINER",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "WELCOME BACK!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Login here",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your email to login for this app",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = emailState,         // Use the state passed from the parent
                onValueChange = onEmailChange,  // Use the callback passed from the parent
                label = { Text("your email @gmail.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState,      // Use the state passed from the parent
                onValueChange = onPasswordChange, // Use the callback passed from the parent
                label = { Text("your password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick, // FIX 2: This should trigger the login action
                enabled = isLoginButtonEnabled, // FIX 3: Use the enabled state passed from parent
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Login", fontSize = 16.sp, color = Color.White) // Changed text to "Login"
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes content below to the bottom

            Text(
                text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                ClickableText(
                    text = AnnotatedString("signup here"),
                    onClick = { onSignUpClick() }, // Use the correct callback
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FitnessTrainerAppTheme {
        LoginScreen(
            emailState = "preview@email.com",
            passwordState = "password",
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUpClick = {},
            isLoginButtonEnabled = true // Updated parameter name
        )
    }
}
