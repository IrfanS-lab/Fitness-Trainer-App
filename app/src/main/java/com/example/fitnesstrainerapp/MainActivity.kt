package com.example.fitnesstrainerapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnesstrainerapp.screen1.CreateAccountScreen
import com.example.fitnesstrainerapp.screen1.LoginScreen
import com.example.fitnesstrainerapp.screen1.WelcomeScreen
import com.example.fitnesstrainerapp.screen2.CustomPlanScreen
import com.example.fitnesstrainerapp.screen2.HomeScreen
import com.example.fitnesstrainerapp.screen2.InboxScreen
import com.example.fitnesstrainerapp.screen2.SettingsScreen
import com.example.fitnesstrainerapp.screen2.ScheduleScreen
import com.example.fitnesstrainerapp.screen2.ProfileScreen
import com.example.fitnesstrainerapp.screen2.DietPlanScreen
import com.example.fitnesstrainerapp.screen2.LiveSessionScreen
import com.example.fitnesstrainerapp.screen2.AddItemScreen
import com.example.fitnesstrainerapp.screen2.Notification
import com.example.fitnesstrainerapp.screen3.CaloriesCalculatorScreen
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            FitnessTrainerAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {

                    /* ---------------- AUTH FLOW ---------------- */

                    composable("welcome") {
                        WelcomeScreen(
                            onLoginClick = { navController.navigate("login") },
                            onSignUpClick = { navController.navigate("create_account") }
                        )
                    }

                    composable("create_account") {
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        val isButtonEnabled = email.isNotEmpty() && password.isNotEmpty()

                        CreateAccountScreen(
                            emailState = email,
                            passwordState = password,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            isContinueButtonEnabled = isButtonEnabled,
                            onContinueClick = {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            navController.navigate("home/$email") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Authentication failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            },
                            onLoginClick = { navController.navigate("login") }
                        )
                    }

                    composable("login") {
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        val isLoginEnabled = email.isNotEmpty() && password.isNotEmpty()

                        LoginScreen(
                            emailState = email,
                            passwordState = password,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            isLoginButtonEnabled = isLoginEnabled,
                            onLoginClick = {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            navController.navigate("home/$email") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Authentication failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            },
                            onSignUpClick = { navController.navigate("create_account") }
                        )
                    }

                    /* ---------------- HOME ---------------- */

                    composable(
                        route = "home/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userEmail = backStackEntry.arguments?.getString("email") ?: ""

                        HomeScreen(
                            onNavItemClick = { route ->
                                val destination = if (route == "profile" || route == "settings") {
                                    "$route/$userEmail"
                                } else {
                                    route
                                }
                                navController.navigate(destination) {
                                    launchSingleTop = true
                                }
                            },
                            onCategoryClick = { route ->
                                navController.navigate(route)
                            },
                            onChallengeClick = { challengeId -> /* Handle Challenge Click */ }
                        )
                    }

                    /* ---------------- INBOX ---------------- */

                    composable("notifications") {
                        val db = Firebase.firestore
                        var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
                        val trainers = listOf("David", "Sarah", "Emily", "John") // You can fetch this from Firebase too

                        // Fetch initial messages
                        LaunchedEffect(Unit) {
                            db.collection("messages")
                                .orderBy("timestamp")
                                .addSnapshotListener { snapshots, e ->
                                    if (e != null) {
                                        // Handle error
                                        return@addSnapshotListener
                                    }

                                    val messageList = snapshots?.mapNotNull {
                                        it.toObject<Notification>()
                                    } ?: emptyList()
                                    notifications = messageList
                                }
                        }

                        InboxScreen(
                            notifications = notifications,
                            onNavigateBack = { navController.popBackStack() },
                            onDeleteItemClick = { senderName ->
                                db.collection("messages").whereEqualTo("senderName", senderName)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            db.collection("messages").document(document.id).delete()
                                        }
                                    }
                            },
                            onMessageSend = { recipient, message ->
                                val newMessage = Notification(
                                    senderName = recipient,
                                    message = message,
                                    timestamp = System.currentTimeMillis(),
                                    senderImageUrl = "" // Add image URL if you have it
                                )
                                db.collection("messages").add(newMessage)
                            },
                            trainers = trainers
                        )
                    }

                    /* ---------------- PROFILE ---------------- */

                    composable(
                        route = "profile/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userEmail = backStackEntry.arguments?.getString("email") ?: ""
                        ProfileScreen(
                            userEmail = userEmail,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------------- SETTINGS ---------------- */

                    composable(
                        route = "settings/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userEmail = backStackEntry.arguments?.getString("email") ?: ""
                        SettingsScreen(
                            userEmail = userEmail,
                            onNavigateBack = { navController.popBackStack() },
                            onLogoutClick = {
                                auth.signOut()
                                navController.navigate("welcome") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }

                    /* ---------------- OTHER SCREENS ---------------- */

                    composable("schedule") {
                        ScheduleScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onAddItemClick = { navController.navigate("add_item") }
                        )
                    }

                    composable("add_item") {
                        AddItemScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("diet_plan") {
                        DietPlanScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onCaloriesCalculatorClick = { navController.navigate("calories_calculator") }
                        )
                    }

                    composable("live_session") {
                        LiveSessionScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    composable("custom_plan") {
                        val currentUserEmail = auth.currentUser?.email ?: ""
                        CustomPlanScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToProfile = {
                                navController.navigate("profile/$currentUserEmail") {
                                    popUpTo("home/$currentUserEmail")
                                }
                            }
                        )
                    }

                    composable("calories_calculator") {
                        CaloriesCalculatorScreen(onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}