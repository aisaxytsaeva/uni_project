package com.example.uni_project.presentation.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.SessionManager
import com.example.uni_project.presentation.viewmodel.DocumentUploadViewModel

class DocumentUploadViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentUploadViewModel::class.java)) {
            return DocumentUploadViewModel(authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}