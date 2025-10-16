package com.example.uni_project.presentation.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.SessionManager
import com.example.uni_project.presentation.viewmodel.RegistrationViewModel

class RegistrationViewModelFactory(private val authRepository: AuthRepository,
                                   private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}