package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uni_project.dao.UserEntity
import com.example.uni_project.core.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasActiveSession = MutableStateFlow(false)
    val hasActiveSession: StateFlow<Boolean> = _hasActiveSession.asStateFlow()

    init {
        checkActiveSession()
    }

    fun checkActiveSession() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                _hasActiveSession.value = user != null
            } catch (e: Exception) {
                _hasActiveSession.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _hasActiveSession.value = false
        }
    }

    fun updateUser(user: UserEntity) {
        _currentUser.value = user
    }
}