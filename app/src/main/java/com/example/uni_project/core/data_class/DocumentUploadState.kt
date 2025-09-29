package com.example.uni_project.core.data_class


data class DocumentUploadState(
    val profilePhotoUri: String? = null,
    val driverLicenseNumber: String = "",
    val driverLicenseIssueDate: String = "",
    val driverLicensePhotoUri: String? = null,
    val passportPhotoUri: String? = null,
    val isLoading: Boolean = false,
    val driverLicenseError: String? = null,
    val issueDateError: String? = null,
    val driverLicensePhotoError: String? = null,
    val passportPhotoError: String? = null
)