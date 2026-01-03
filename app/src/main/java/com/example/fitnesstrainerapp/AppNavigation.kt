package com.example.fitnesstrainerapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Login Screen
        composable("login") {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginScreen(
                emailState = email,
                passwordState = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = { navController.navigate("createAccount") },
                onSignUpClick = { navController.navigate("createAccount") },
                onContinueClick = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Create Account Screen
        composable("createAccount") {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            CreateAccountScreen(
                emailState = email,
                passwordState = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = { navController.navigate("login") },
                onContinueClick = {
                    navController.navigate("main") {
                        popUpTo("createAccount") { inclusive = true }
                    }
                }
            )
        }

        // Main Page
        composable("main") {
            MainPage()
        }
    }
}

@Composable
fun LoginScreen(
    emailState: String,
    passwordState: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("FITNESS TRAINER", fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("WELCOME BACK!", fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = emailState,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button enabled only if email and password are filled
            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = emailState.isNotBlank() && passwordState.isNotBlank()
            ) {
                Text("Continue", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                ClickableText(
                    text = AnnotatedString("signup here"),
                    onClick = { onSignUpClick() },
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun CreateAccountScreen(
    emailState: String,
    passwordState: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("FITNESS TRAINER", fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("CREATE AN ACCOUNT", fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = emailState,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = emailState.isNotBlank() && passwordState.isNotBlank()
            ) {
                Text("Continue", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                ClickableText(
                    text = AnnotatedString("login here"),
                    onClick = { onLoginClick() },
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun MainPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Welcome to the Main Page!", fontSize = 24.sp)
    }
}
