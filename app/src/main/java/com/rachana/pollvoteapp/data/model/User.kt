package com.rachana.pollvoteapp.data.model
// What it does: Defines what information we store about each user.
//Why we need it: Firebase needs to know the structure of user data

/**
 * Represents a user in the app
 * This is like a "user profile card" with their info
 */
data class User(
    val id: String = "",           // Unique user ID from Firebase
    val name: String = "",         // Display name (e.g., "Rachana")
    val email: String = "",        // Email address
    val createdAt: Long = System.currentTimeMillis()  // When they registered
)
