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
fun CreateAccountScreen(
    emailState: String,
    passwordState: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    onLoginClick: () -> Unit,
    isContinueButtonEnabled: Boolean //Add this parameter
) {
    // FIX: REMOVED the local state variables.
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
                .verticalScroll(rememberScrollState()),
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
                text = "CREATE AN ACCOUNT",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Create an account",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your email to sign up for this app",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = emailState, // Correctly uses the state from MainActivity
                onValueChange = onEmailChange, // Correctly calls the lambda from MainActivity
                label = { Text("your email @gmail.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState, // Correctly uses the state from MainActivity
                onValueChange = onPasswordChange, // Correctly calls the lambda from MainActivity
                label = { Text("your password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinueClick,
                enabled = isContinueButtonEnabled, // Correctly calls the onContinueClick from MainActivity
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Continue", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

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
                Text("Already have account", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                ClickableText(
                    text = AnnotatedString("login here"),
                    onClick = {
                        onLoginClick()
                    },
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
fun CreateAccountScreenPreview() {
    FitnessTrainerAppTheme {
        CreateAccountScreen(
            emailState = "example@email.com",
            passwordState = "password",
            onEmailChange = {},
            onPasswordChange = {},
            onContinueClick = {},
            onLoginClick = {},
            isContinueButtonEnabled = true //Add this parameter
        )
    }
}
