package com.example.fitnesstrainerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// 2. Suppress the warning for custom splash screen logic
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Install the splash screen - this should be called before setContentView
        installSplashScreen()

        // 4. Navigate to your main content
        // The splash screen will be displayed until your app is ready to draw the first frame.
        // No need for a manual Handler with a delay.
        navigateToMain()
    }

    private fun navigateToMain() {
        // Create an Intent to start the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Close the SplashActivity so the user can't navigate back to it
        finish()
    }
}
