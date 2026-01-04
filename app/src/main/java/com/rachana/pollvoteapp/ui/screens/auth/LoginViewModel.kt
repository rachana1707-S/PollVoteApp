//What it does: Handles login screen logic (email, password, login button).
//Why we need it: Separates login logic from UI. Keeps screen code clean.
package com.rachana.pollvoteapp.ui.screens.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachana.pollvoteapp.data.repository.AuthRepository
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages login screen state and logic
 * Think of this as the "Login Manager"
 */
class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // UI State - what the screen should display
    private val _loginState = MutableStateFlow<Resource<String>?>(null)
    val loginState: StateFlow<Resource<String>?> = _loginState

    // User input fields
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    /**
     * Update email as user types
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Update password as user types
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Called when user clicks "Login" button
     * Does all the login work in background
     */
    fun login() {
        // Validate inputs first
        if (_email.value.isBlank()) {
            _loginState.value = Resource.Error("Email cannot be empty")
            return
        }

        if (_password.value.isBlank()) {
            _loginState.value = Resource.Error("Password cannot be empty")
            return
        }

        // Start login process
        viewModelScope.launch {
            _loginState.value = Resource.Loading()

            val result = authRepository.login(
                email = _email.value,
                password = _password.value
            )

            _loginState.value = when (result) {
                is Resource.Success -> Resource.Success("Login successful")
                is Resource.Error -> Resource.Error(result.message ?: "Login failed")
                else -> null
            }
        }
    }

    /**
     * Reset state after showing error/success
     */
    fun resetState() {
        _loginState.value = null
    }
}