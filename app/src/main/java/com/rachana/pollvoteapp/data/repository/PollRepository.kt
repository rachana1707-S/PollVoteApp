package com.rachana.pollvoteapp.data.repository
// Handles all poll operations (create, get, delete polls).
//Why we need it: This is the heart of our polling app! Manages all poll data.

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rachana.pollvoteapp.data.model.Poll
import com.rachana.pollvoteapp.data.model.Vote
import com.rachana.pollvoteapp.data.model.PollWithResults
import com.rachana.pollvoteapp.utils.Constants
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Manages all poll operations
 * Think of this as your "poll manager"
 */
class PollRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Create a new poll
     * Returns the poll ID if successful
     */
    suspend fun createPoll(poll: Poll): Resource<String> {
        return try {
            val docRef = firestore.collection(Constants.POLLS_COLLECTION)
                .add(poll)
                .await()

            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create poll")
        }
    }

    /**
     * Get all polls with REAL-TIME updates
     * Flow = Stream of data that updates automatically
     * When someone creates/deletes a poll, everyone sees it instantly!
     */
    fun getAllPolls(): Flow<Resource<List<Poll>>> = callbackFlow {
        // Send loading state first
        trySend(Resource.Loading())

        // Listen to Firestore changes in real-time
        val listener = firestore.collection(Constants.POLLS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING) // Newest first
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to get polls"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Convert Firestore documents to Poll objects
                    val polls = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Poll::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(polls))
                }
            }

        // Remove listener when Flow is cancelled
        awaitClose { listener.remove() }
    }

    /**
     * Get a single poll by ID
     */
    suspend fun getPollById(pollId: String): Resource<Poll> {
        return try {
            val doc = firestore.collection(Constants.POLLS_COLLECTION)
                .document(pollId)
                .get()
                .await()

            val poll = doc.toObject(Poll::class.java)?.copy(id = doc.id)
                ?: throw Exception("Poll not found")

            Resource.Success(poll)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get poll")
        }
    }

    /**
     * Get poll WITH vote counts - REAL-TIME!
     * This is what makes results update live as people vote!
     */
    fun getPollWithResults(pollId: String, userId: String): Flow<Resource<PollWithResults>> = callbackFlow {
        trySend(Resource.Loading())

        // Listen to poll changes
        val pollListener = firestore.collection(Constants.POLLS_COLLECTION)
            .document(pollId)
            .addSnapshotListener { pollDoc, error ->

                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to get poll"))
                    return@addSnapshotListener
                }

                if (pollDoc != null && pollDoc.exists()) {
                    val poll = pollDoc.toObject(Poll::class.java)?.copy(id = pollDoc.id)

                    if (poll != null) {
                        // Listen to votes for this poll
                        firestore.collection(Constants.VOTES_COLLECTION)
                            .whereEqualTo("pollId", pollId)
                            .addSnapshotListener { votesSnapshot, votesError ->

                                if (votesError != null) {
                                    trySend(Resource.Error(votesError.message ?: "Failed to get votes"))
                                    return@addSnapshotListener
                                }

                                if (votesSnapshot != null) {
                                    val votes = votesSnapshot.documents.mapNotNull {
                                        it.toObject(Vote::class.java)
                                    }

                                    // Count votes for each option
                                    val voteCounts = mutableMapOf<Int, Int>()
                                    var userVote: Int? = null

                                    votes.forEach { vote ->
                                        // Increment count for this option
                                        voteCounts[vote.optionIndex] =
                                            voteCounts.getOrDefault(vote.optionIndex, 0) + 1

                                        // Check if current user voted
                                        if (vote.userId == userId) {
                                            userVote = vote.optionIndex
                                        }
                                    }

                                    // Combine poll + results
                                    val pollWithResults = PollWithResults(
                                        poll = poll,
                                        voteCounts = voteCounts,
                                        totalVotes = votes.size,
                                        userVote = userVote
                                    )

                                    trySend(Resource.Success(pollWithResults))
                                }
                            }
                    }
                }
            }

        awaitClose { pollListener.remove() }
    }

    /**
     * Delete a poll (only creator can delete)
     */
    suspend fun deletePoll(pollId: String): Resource<Unit> {
        return try {
            firestore.collection(Constants.POLLS_COLLECTION)
                .document(pollId)
                .delete()
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete poll")
        }
    }
}