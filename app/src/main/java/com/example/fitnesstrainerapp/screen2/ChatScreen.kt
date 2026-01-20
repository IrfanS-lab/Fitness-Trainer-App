package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen(senderName: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Chat with $senderName")
    }
}
