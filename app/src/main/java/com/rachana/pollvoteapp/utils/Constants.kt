//What it does: Stores all constant values used across the app.
//Why we need it: Single place to change values. Avoids typos.
package com.rachana.pollvoteapp.utils

/**
 * App-wide constants
 * Think of this as your "settings file"
 */
object Constants {
    // Firebase collection names
    // These are like "folder names" in Firebase database
    const val USERS_COLLECTION = "users"
    const val POLLS_COLLECTION = "polls"
    const val VOTES_COLLECTION = "votes"

    // Navigation routes (we'll use these later)
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_HOME = "home"
    const val ROUTE_CREATE_POLL = "create_poll"
    const val ROUTE_POLL_DETAIL = "poll_detail/{pollId}"
    const val ROUTE_PROFILE = "profile"
}