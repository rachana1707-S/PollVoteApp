//What it does: Handles registration screen logic.
//Why we need it: Similar to LoginViewModel but for creating new accounts.
package com.rachana.pollvoteapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachana.pollvoteapp.data.repository.AuthRepository
import com.rachana.pollvoteapp.utils.Resource
import com.rachana.pollvoteapp.utils.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages registration screen
 * Think of this as the "Sign Up Manager"
 */
class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // UI State
    private val _registerState = MutableStateFlow<Resource<String>?>(null)
    val registerState: StateFlow<Resource<String>?> = _registerState

    // User input fields
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    /**
     * Update functions for each field
     */
    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    /**
     * Called when user clicks "Register" button
     * Validates all inputs then creates account
     */
    fun register() {
        // Validation
        if (_name.value.isBlank()) {
            _registerState.value = Resource.Error("Name cannot be empty")
            return
        }

        if (_email.value.isBlank()) {
            _registerState.value = Resource.Error("Email cannot be empty")
            return
        }

        if (!_email.value.isValidEmail()) {
            _registerState.value = Resource.Error("Please enter a valid email")
            return
        }

        if (_password.value.length < 6) {
            _registerState.value = Resource.Error("Password must be at least 6 characters")
            return
        }

        if (_password.value != _confirmPassword.value) {
            _registerState.value = Resource.Error("Passwords do not match")
            return
        }

        // Start registration
        viewModelScope.launch {
            _registerState.value = Resource.Loading()

            val result = authRepository.register(
                name = _name.value,
                email = _email.value,
                password = _password.value
            )

            _registerState.value = when (result) {
                is Resource.Success -> Resource.Success("Registration successful")
                is Resource.Error -> Resource.Error(result.message ?: "Registration failed")
                else -> null
            }
        }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _registerState.value = null
    }
}