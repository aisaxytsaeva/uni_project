package com.example.uni_project.core.data_class

sealed class UserProfileState {
    object Loading : UserProfileState()
    data class Success(val userProfile: UserProfile) : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}