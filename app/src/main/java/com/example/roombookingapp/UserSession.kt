package com.example.roombookingapp

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun login(userId: Int, email: String) {
        prefs.edit().apply {
            putInt("user_id", userId)
            putString("email", email)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getEmail(): String? = prefs.getString("email", null)
}
