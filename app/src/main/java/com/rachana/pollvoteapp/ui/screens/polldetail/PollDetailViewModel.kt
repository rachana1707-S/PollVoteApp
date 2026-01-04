//What it does: Manages a single poll screen - voting and seeing results in real-time!
//Why we need it: This is where the magic happens - live vote counting!

package com.rachana.pollvoteapp.ui.screens.polldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachana.pollvoteapp.data.model.PollWithResults
import com.rachana.pollvoteapp.data.repository.AuthRepository
import com.rachana.pollvoteapp.data.repository.PollRepository
import com.rachana.pollvoteapp.data.repository.VoteRepository
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages individual poll screen
 * Shows poll + results with LIVE updates!
 */
class PollDetailViewModel : ViewModel() {

    private val pollRepository = PollRepository()
    private val voteRepository = VoteRepository()
    private val authRepository = AuthRepository()

    // Poll with live results
    private val _pollWithResults = MutableStateFlow<Resource<PollWithResults>>(Resource.Loading())
    val pollWithResults: StateFlow<Resource<PollWithResults>> = _pollWithResults

    // Vote action state
    private val _voteState = MutableStateFlow<Resource<String>?>(null)
    val voteState: StateFlow<Resource<String>?> = _voteState

    // Current user ID
    private val currentUserId = authRepository.getCurrentUserId() ?: ""

    /**
     * Load poll with real-time results
     * Every time someone votes, results update automatically!
     */
    fun loadPoll(pollId: String) {
        viewModelScope.launch {
            pollRepository.getPollWithResults(pollId, currentUserId).collect { resource ->
                _pollWithResults.value = resource
            }
        }
    }

    /**
     * Cast a vote
     * Example: vote(pollId, 0) votes for first option
     */
    fun vote(pollId: String, optionIndex: Int, allowMultipleVotes: Boolean) {
        viewModelScope.launch {
            _voteState.value = Resource.Loading()

            val result = voteRepository.vote(
                pollId = pollId,
                userId = currentUserId,
                optionIndex = optionIndex,
                allowMultipleVotes = allowMultipleVotes
            )

            _voteState.value = when (result) {
                is Resource.Success -> Resource.Success("Vote recorded!")
                is Resource.Error -> Resource.Error(result.message ?: "Failed to vote")
                else -> null
            }
        }
    }

    /**
     * Reset vote state
     */
    fun resetVoteState() {
        _voteState.value = null
    }
}

