package com.example.uni_project.core.data_class

sealed class RegistrationResult {
    data class Success(val message: String) : RegistrationResult()
    data class Error(val message: String) : RegistrationResult()
}