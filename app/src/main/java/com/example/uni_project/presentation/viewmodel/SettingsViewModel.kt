package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.data_class.UserProfile
import com.example.uni_project.core.data_class.UserProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = UserProfileState.Loading

                if (authRepository != null) {

                    val currentUser = authRepository.getCurrentUser()
                    if (currentUser != null) {
                        _userProfile.value = UserProfileState.Success(
                            UserProfile(
                                userName = "${currentUser.firstName} ${currentUser.lastName}".trim(),
                                userEmail = currentUser.email,
                                avatarUrl = currentUser.profilePhotoUri
                            )
                        )
                    } else {
                        _userProfile.value = UserProfileState.Error("Пользователь не авторизован")
                    }
                } else {

                    _userProfile.value = UserProfileState.Success(
                        UserProfile(
                            userName = "Иван Иванов",
                            userEmail = "user@example.com",
                            avatarUrl = null
                        )
                    )
                }

            } catch (e: Exception) {
                _userProfile.value = UserProfileState.Error("Ошибка загрузки профиля: ${e.message}")
            }
        }
    }

    fun refreshUserProfile() {
        loadUserProfile()
    }
}