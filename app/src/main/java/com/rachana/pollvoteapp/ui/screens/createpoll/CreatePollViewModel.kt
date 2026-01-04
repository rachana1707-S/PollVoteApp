//What it does: Handles creating new polls.
//Why we need it: Manages poll question, options, and creation logic.
package com.rachana.pollvoteapp.ui.screens.createpoll


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachana.pollvoteapp.data.model.Poll
import com.rachana.pollvoteapp.data.repository.AuthRepository
import com.rachana.pollvoteapp.data.repository.PollRepository
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages poll creation screen
 * Think of this as the "Poll Creator"
 */
class CreatePollViewModel : ViewModel() {

    private val pollRepository = PollRepository()
    private val authRepository = AuthRepository()

    // UI State
    private val _createState = MutableStateFlow<Resource<String>?>(null)
    val createState: StateFlow<Resource<String>?> = _createState

    // Poll inputs
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> = _question

    // List of options (starts with 2 empty options)
    private val _options = MutableStateFlow(listOf("", ""))
    val options: StateFlow<List<String>> = _options

    private val _allowMultipleVotes = MutableStateFlow(false)
    val allowMultipleVotes: StateFlow<Boolean> = _allowMultipleVotes

    /**
     * Update question
     */
    fun onQuestionChange(newQuestion: String) {
        _question.value = newQuestion
    }

    /**
     * Update a specific option
     * Example: updateOption(0, "Pizza") updates first option
     */
    fun updateOption(index: Int, value: String) {
        val currentOptions = _options.value.toMutableList()
        if (index < currentOptions.size) {
            currentOptions[index] = value
            _options.value = currentOptions
        }
    }

    /**
     * Add another option (max 10)
     */
    fun addOption() {
        if (_options.value.size < 10) {
            _options.value = _options.value + ""
        }
    }

    /**
     * Remove an option (minimum 2 required)
     */
    fun removeOption(index: Int) {
        if (_options.value.size > 2) {
            val currentOptions = _options.value.toMutableList()
            currentOptions.removeAt(index)
            _options.value = currentOptions
        }
    }

    /**
     * Toggle multiple votes option
     */
    fun toggleMultipleVotes() {
        _allowMultipleVotes.value = !_allowMultipleVotes.value
    }

    /**
     * Create the poll
     */
    fun createPoll() {
        // Validation
        if (_question.value.isBlank()) {
            _createState.value = Resource.Error("Question cannot be empty")
            return
        }

        // Filter out empty options
        val validOptions = _options.value.filter { it.isNotBlank() }

        if (validOptions.size < 2) {
            _createState.value = Resource.Error("Please provide at least 2 options")
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _createState.value = Resource.Error("You must be logged in to create a poll")
            return
        }

        // Create poll object
        val poll = Poll(
            question = _question.value,
            options = validOptions,
            createdBy = userId,
            allowMultipleVotes = _allowMultipleVotes.value
        )

        // Save to Firebase
        viewModelScope.launch {
            _createState.value = Resource.Loading()

            val result = pollRepository.createPoll(poll)

            _createState.value = when (result) {
                is Resource.Success -> Resource.Success(result.data ?: "")
                is Resource.Error -> Resource.Error(result.message ?: "Failed to create poll")
                else -> null
            }
        }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _createState.value = null
    }
}