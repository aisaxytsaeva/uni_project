package com.example.uni_project.presentation.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.data_class.AuthRepository
import com.example.uni_project.presentation.viewmodel.AuthViewModel


class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val database = AppDatabase.getInstance(context)
            val googleAuthService = GoogleAuthService(context)
            val authRepository: AuthRepository = AuthRepositoryImpl(database, googleAuthService, context)
            return AuthViewModel(authRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}