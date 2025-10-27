package com.example.uni_project.core.data_class

import com.example.uni_project.dao.AuthProvider

data class User(
    val email: String,
    val password: String?,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val birthDate: String,
    val gender: String,
    val profilePhotoUri: String?,
    val driverLicenseNumber: String,
    val driverLicenseIssueDate: String,
    val driverLicensePhotoUri: String?,
    val passportPhotoUri: String?,
    val authProvider: AuthProvider,
    val createdAt: Any,
    val lastLogin: Long,
    val registrationStep: Int,
    val registrationCompleted: Boolean,
    val documentsUploaded: Boolean
)