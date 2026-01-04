//What it does: Helper functions to make code cleaner.
//Why we need it: Reusable utility functions.
package com.rachana.pollvoteapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension functions - add functionality to existing classes
 * Think of these as "helper tools"
 */

/**
 * Convert timestamp (milliseconds) to readable date
 * Example: 1704153600000 → "Jan 02, 2024 10:30"
 */
fun Long.toDateString(): String {
    val date = Date(this)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

/**
 * Check if string is a valid email
 * Example: "test@email.com" → true, "notanemail" → false
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if timestamp has expired
 * Used for poll expiration
 */
fun Long?.isExpired(): Boolean {
    return this != null && this < System.currentTimeMillis()
}
