package com.rachana.pollvoteapp.data.model

/**
 * Poll + its results combined
 * Makes it easy to display poll with vote counts and percentages
 */
data class PollWithResults(
    val poll: Poll,                           // The poll itself
    val voteCounts: Map<Int, Int> = emptyMap(), // Map of option index to vote count
    val totalVotes: Int = 0,                  // Total number of votes
    val userVote: Int? = null                 // Which option current user voted for (if any)
) {
    /**
     * Calculate percentage for a specific option
     * Example: Option 0 has 15 votes out of 50 total = 30%
     */
    fun getPercentage(optionIndex: Int): Float {
        if (totalVotes == 0) return 0f
        val votes = voteCounts[optionIndex] ?: 0
        return (votes.toFloat() / totalVotes.toFloat()) * 100f
    }
}