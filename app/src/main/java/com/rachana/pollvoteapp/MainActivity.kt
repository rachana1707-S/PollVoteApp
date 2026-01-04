package com.rachana.pollvoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rachana.pollvoteapp.ui.screens.auth.LoginScreen
import com.rachana.pollvoteapp.ui.theme.PollVoteAppTheme

/**
 * Main entry point of the app
 * This runs when you open the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Wrap everything in our theme
            PollVoteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Show login screen for now
                    LoginScreen(
                        onLoginSuccess = {
                            // TODO: Navigate to home (we'll add this later)
                        },
                        onNavigateToRegister = {
                            // TODO: Navigate to register (we'll add this later)
                        }
                    )
                }
            }
        }
    }
}