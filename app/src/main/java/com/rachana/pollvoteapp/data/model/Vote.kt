package com.rachana.pollvoteapp.data.model
//What it does: Represents a single vote.
//Why we need it: Tracks who voted for what.

/**
 * Represents a single vote
 * Example: User123 voted for option 0 (first option) on Poll456
 */
data class Vote(
    val id: String = "",                      // Unique vote ID
    val pollId: String = "",                  // Which poll this vote is for
    val userId: String = "",                  // Who voted
    val optionIndex: Int = 0,                 // Which option they chose (0, 1, 2...)
    val timestamp: Long = System.currentTimeMillis()  // When they voted
)