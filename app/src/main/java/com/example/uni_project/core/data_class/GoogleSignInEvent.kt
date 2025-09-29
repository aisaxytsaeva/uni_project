package com.example.uni_project.core.data_class

import android.content.Intent

sealed class GoogleSignInEvent {
    data class LaunchSignIn(val signInIntent: Intent) : GoogleSignInEvent()
    object Success : GoogleSignInEvent()
    data class Error(val message: String) : GoogleSignInEvent()
}