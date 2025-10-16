package com.example.uni_project.presentation.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.SessionManager
import com.example.uni_project.presentation.viewmodel.AuthViewModel


class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}