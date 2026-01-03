package com.rachana.pollvoteapp.data.repository
// Handles voting - casting votes and checking if user already voted.
//Why we need it: Prevents duplicate votes and manages vote records.

import com.google.firebase.firestore.FirebaseFirestore
import com.rachana.pollvoteapp.data.model.Vote
import com.rachana.pollvoteapp.utils.Constants
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.tasks.await

/**
 * Handles voting operations
 * Think of this as your "vote counter"
 */
class VoteRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Cast a vote
     * Checks if user already voted (unless multiple votes allowed)
     */
    suspend fun vote(
        pollId: String,
        userId: String,
        optionIndex: Int,
        allowMultipleVotes: Boolean
    ): Resource<Unit> {
        return try {
            // Check if user already voted (if multiple votes not allowed)
            if (!allowMultipleVotes) {
                val existingVote = firestore.collection(Constants.VOTES_COLLECTION)
                    .whereEqualTo("pollId", pollId)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!existingVote.isEmpty) {
                    return Resource.Error("You have already voted on this poll")
                }
            }

            // Create the vote
            val vote = Vote(
                pollId = pollId,
                userId = userId,
                optionIndex = optionIndex,
                timestamp = System.currentTimeMillis()
            )

            // Save vote to Firestore
            firestore.collection(Constants.VOTES_COLLECTION)
                .add(vote)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to vote")
        }
    }

    /**
     * Check if user has already voted on a poll
     */
    suspend fun hasUserVoted(pollId: String, userId: String): Resource<Boolean> {
        return try {
            val votes = firestore.collection(Constants.VOTES_COLLECTION)
                .whereEqualTo("pollId", pollId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Resource.Success(!votes.isEmpty)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to check vote status")
        }
    }
}