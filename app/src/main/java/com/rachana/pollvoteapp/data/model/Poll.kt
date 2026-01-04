package com.rachana.pollvoteapp.data.model
//What it does: Defines the structure of a poll.
//Why we need it: Tells Firebase what a poll looks like.

/**
 * Represents a poll/survey
 * Example: "What's your favorite food?" with options ["Pizza", "Burger", "Pasta"]
 */
data class Poll(
    val id: String = "",                      // Unique poll ID
    val question: String = "",                // The poll question
    val options: List<String> = emptyList(),  // List of choices ["Option 1", "Option 2", ...]
    val createdBy: String = "",               // User ID who created it
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,              // Optional: when poll closes
    val isActive: Boolean = true,             // Is poll still accepting votes?
    val allowMultipleVotes: Boolean = false   // Can users vote multiple times?
)