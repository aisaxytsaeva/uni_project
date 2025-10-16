package com.example.uni_project.presentation.viewmodel



import androidx.lifecycle.ViewModel
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.data_class.LoginResult
import com.example.uni_project.core.data_class.LoginState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.activity.result.ActivityResult
import com.example.uni_project.core.SessionManager

import com.example.uni_project.core.data_class.GoogleSignInEvent
import kotlinx.coroutines.flow.*




class AuthViewModel(
    private val authRepository: AuthRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Заменяем на SharedFlow для событий
    private val _googleSignInEvent = MutableSharedFlow<GoogleSignInEvent>()
    val googleSignInEvent: SharedFlow<GoogleSignInEvent> = _googleSignInEvent.asSharedFlow()

    // Валидация email
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailPattern.matches(email)
    }

    val isFormValid: Boolean
        get() = _loginState.value.email.isNotBlank() &&
                _loginState.value.password.isNotBlank() &&
                isValidEmail(_loginState.value.email) &&
                _loginState.value.password.length >= 6

    fun updateEmail(email: String) {
        _loginState.value = _loginState.value.copy(email = email.trim(), error = null)
    }

    fun updatePassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password, error = null)
    }

    fun togglePasswordVisibility() {
        _loginState.value = _loginState.value.copy(
            isPasswordVisible = !_loginState.value.isPasswordVisible
        )
    }

    fun clearError() {
        _loginState.value = _loginState.value.copy(error = null)
    }

    fun login(onResult: (LoginResult) -> Unit) {
        if (!isFormValid) {
            _loginState.value = _loginState.value.copy(
                error = "Пожалуйста, заполните все поля корректно"
            )
            onResult(LoginResult(success = false, error = "Invalid form"))
            return
        }

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)

            val result = authRepository.login(
                email = _loginState.value.email,
                password = _loginState.value.password
            )

            if (result.success) {

                saveLoginData(_loginState.value.email)
            }

            _loginState.value = _loginState.value.copy(
                isLoading = false,
                error = result.error
            )

            onResult(result)
        }
    }

    fun prepareGoogleSignIn() {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)

            try {
                val intent = authRepository.getGoogleSignInIntent()
                _googleSignInEvent.emit(GoogleSignInEvent.LaunchSignIn(intent))
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = "Ошибка подготовки Google Sign-In: ${e.message}"
                )
            }
        }
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)

            try {
                val authResult = authRepository.handleGoogleSignInResult(result)

                if (authResult.success) {

                    saveLoginData(_loginState.value.email)
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    _googleSignInEvent.emit(GoogleSignInEvent.Success)
                } else {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = authResult.error ?: "Ошибка авторизации через Google"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = "Ошибка обработки Google Sign-In: ${e.message}"
                )
            }
        }
    }
    private fun saveLoginData(email: String) {

    }
}


