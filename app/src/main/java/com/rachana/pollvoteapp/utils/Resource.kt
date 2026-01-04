//What it does: Wraps API responses to handle loading, success, and error states.
//Why we need it: Makes it easy to show loading spinners and error messages.
package com.rachana.pollvoteapp.utils


/**
 * Wrapper for handling async operations
 * Think of it as a "status report" for network requests
 *
 * Example usage:
 * - Resource.Loading() → Show loading spinner
 * - Resource.Success(data) → Show the data
 * - Resource.Error("Failed") → Show error message
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}