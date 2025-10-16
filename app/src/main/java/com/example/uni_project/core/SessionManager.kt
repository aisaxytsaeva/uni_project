package com.example.uni_project.core

import android.content.Context

class SessionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_USER_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && getAuthToken() != null
    }

    fun clearAuthToken() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_TOKEN)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }



    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }



    fun saveAuthData(token: String, email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_TOKEN, token)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    fun clearAuthData() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_TOKEN)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_EMAIL)
            apply()
        }
        println("🟢 DEBUG SessionManager: All auth data cleared")
    }




}