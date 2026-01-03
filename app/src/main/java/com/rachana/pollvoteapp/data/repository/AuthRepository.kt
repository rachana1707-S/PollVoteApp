package com.rachana.pollvoteapp.data.repository
// Handles user login, registration, and logout.
// Separates authentication logic from UI.


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rachana.pollvoteapp.data.model.User
import com.rachana.pollvoteapp.utils.Constants
import com.rachana.pollvoteapp.utils.Resource
import kotlinx.coroutines.tasks.await

/**
 * Handles user authentication
 * Think of this as your "login/register manager"
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Register a new user
     * Steps: 1) Create account in Firebase Auth
     *        2) Save user info in Firestore database
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<User> {
        return try {
            // Create user in Firebase Authentication
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            // Create user profile
            val user = User(
                id = userId,
                name = name,
                email = email
            )

            // Save user profile to Firestore
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .set(user)
                .await()

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    /**
     * Login existing user
     */
    suspend fun login(
        email: String,
        password: String
    ): Resource<User> {
        return try {
            // Sign in with Firebase
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            // Get user data from Firestore
            val userDoc = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User not found")

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    /**
     * Logout current user
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}