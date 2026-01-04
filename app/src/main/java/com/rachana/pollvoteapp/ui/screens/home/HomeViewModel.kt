//What it does: Manages the home screen that shows all polls.
//Why we need it: Loads all polls and updates in real-time when new polls are created!
package com.rachana.pollvoteapp.ui.screens.home

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
 * Manages home screen showing all polls
 * This is the "main feed" of your app
 */
class HomeViewModel : ViewModel() {

    private val pollRepository = PollRepository()
    private val authRepository = AuthRepository()

    // List of all polls - updates automatically!
    private val _polls = MutableStateFlow<Resource<List<Poll>>>(Resource.Loading())
    val polls: StateFlow<Resource<List<Poll>>> = _polls

    // Current user ID
    val currentUserId = authRepository.getCurrentUserId() ?: ""

    init {
        // Load polls as soon as ViewModel is created
        loadPolls()
    }

    /**
     * Load all polls with real-time updates
     * When someone creates a poll, it appears automatically!
     */
    private fun loadPolls() {
        viewModelScope.launch {
            pollRepository.getAllPolls().collect { resource ->
                _polls.value = resource
            }
        }
    }

    /**
     * Refresh polls manually (pull to refresh)
     */
    fun refreshPolls() {
        loadPolls()
    }

    /**
     * Logout user
     */
    fun logout() {
        authRepository.logout()
    }
}