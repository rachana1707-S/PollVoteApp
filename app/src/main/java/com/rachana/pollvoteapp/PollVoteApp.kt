// Application class (for initialization)

package com.rachana.pollvoteapp

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Application class - runs when app starts
 * Initializes Firebase
 */
class PollVoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}