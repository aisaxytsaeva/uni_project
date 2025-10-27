package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.data_class.Gender
import com.example.uni_project.core.data_class.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = authRepository.getCurrentUser()

                currentUser?.let { user ->
                    val genderString = when (user.gender) {
                        Gender.MALE -> "Мужской"
                        Gender.FEMALE -> "Женский"
                        Gender.UNSPECIFIED -> "Не указан"
                        else -> "Не указан"
                    }
                    _userProfile.value = User(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email,
                        createdAt = user.createdAt,
                        gender = genderString,
                        password = user.password,
                        middleName = user.middleName,
                        birthDate = user.birthDate,
                        profilePhotoUri = user.profilePhotoUri,
                        driverLicenseNumber = user.driverLicenseNumber,
                        driverLicenseIssueDate = user.driverLicenseIssueDate,
                        driverLicensePhotoUri = user.driverLicensePhotoUri,
                        passportPhotoUri = user.passportPhotoUri,
                        authProvider = user.authProvider,
                        lastLogin = user.lastLogin,
                        registrationStep = user.registrationStep,
                        registrationCompleted = user.registrationCompleted,
                        documentsUploaded = user.documentsUploaded
                    )
                }
            } catch (e: Exception) {

                println("Error loading profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun refreshProfile() {
        loadUserProfile()
    }
}

