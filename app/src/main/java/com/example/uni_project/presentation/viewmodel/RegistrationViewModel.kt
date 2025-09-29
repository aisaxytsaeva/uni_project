package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uni_project.dao.UserEntity
import com.example.uni_project.core.data_class.AuthRepository
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.core.data_class.RegistrationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistrationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registrationState = MutableStateFlow(RegistrationState())
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult.asStateFlow()

    // Валидация email
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailPattern.matches(email)
    }

    // Проверка готовности формы
    val isFormValid: Boolean
        get() = _registrationState.value.email.isNotBlank() &&
                _registrationState.value.password.isNotBlank() &&
                _registrationState.value.confirmPassword.isNotBlank() &&
                _registrationState.value.isTermsAccepted &&
                isValidEmail(_registrationState.value.email) &&
                _registrationState.value.password == _registrationState.value.confirmPassword &&
                _registrationState.value.password.length >= 6

    fun updateEmail(email: String) {
        _registrationState.value = _registrationState.value.copy(
            email = email.trim(),
            emailError = null
        )
    }

    fun updatePassword(password: String) {
        _registrationState.value = _registrationState.value.copy(
            password = password,
            passwordError = null,
            confirmPasswordError = if (password != _registrationState.value.confirmPassword && _registrationState.value.confirmPassword.isNotBlank()) {
                "Пароли не совпадают"
            } else null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _registrationState.value = _registrationState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = if (confirmPassword != _registrationState.value.password && _registrationState.value.password.isNotBlank()) {
                "Пароли не совпадают"
            } else null
        )
    }

    fun togglePasswordVisibility() {
        _registrationState.value = _registrationState.value.copy(
            isPasswordVisible = !_registrationState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _registrationState.value = _registrationState.value.copy(
            isConfirmPasswordVisible = !_registrationState.value.isConfirmPasswordVisible
        )
    }

    fun toggleTermsAccepted() {
        _registrationState.value = _registrationState.value.copy(
            isTermsAccepted = !_registrationState.value.isTermsAccepted,
            termsError = null
        )
    }

    fun clearErrors() {
        _registrationState.value = _registrationState.value.copy(
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            termsError = null
        )
    }

    private fun validateForm(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        // Валидация email
        when {
            _registrationState.value.email.isBlank() -> {
                errors["email"] = "Электронная почта обязательна для заполнения"
            }
            !isValidEmail(_registrationState.value.email) -> {
                errors["email"] = "Введите корректный адрес электронной почты"
            }
        }

        // Валидация пароля
        when {
            _registrationState.value.password.isBlank() -> {
                errors["password"] = "Пароль обязателен для заполнения"
            }
            _registrationState.value.password.length < 6 -> {
                errors["password"] = "Пароль должен содержать не менее 6 символов"
            }
        }

        // Валидация подтверждения пароля
        when {
            _registrationState.value.confirmPassword.isBlank() -> {
                errors["confirmPassword"] = "Подтверждение пароля обязательно"
            }
            _registrationState.value.password != _registrationState.value.confirmPassword -> {
                errors["confirmPassword"] = "Пароли не совпадают"
            }
        }

        // Валидация чекбокса
        if (!_registrationState.value.isTermsAccepted) {
            errors["terms"] = "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности"
        }

        return errors
    }

    suspend fun registerStep1(): Boolean {
        if (!isFormValid) {
            val errors = validateForm()
            _registrationState.value = _registrationState.value.copy(
                emailError = errors["email"],
                passwordError = errors["password"],
                confirmPasswordError = errors["confirmPassword"],
                termsError = errors["terms"]
            )
            return false
        }

        _registrationState.value = _registrationState.value.copy(isLoading = true)

        return try {
            val result = authRepository.registerStep1(
                email = _registrationState.value.email,
                password = _registrationState.value.password
            )

            if (result.success) {
                _registrationResult.value = RegistrationResult.Success(
                    message = "Первый шаг завершен. Переход к дополнительным данным."
                )
                true
            } else {
                _registrationResult.value = RegistrationResult.Error(result.error ?: "Ошибка регистрации")
                false
            }
        } catch (e: Exception) {
            _registrationResult.value = RegistrationResult.Error("Ошибка сети: ${e.message}")
            false
        } finally {
            _registrationState.value = _registrationState.value.copy(isLoading = false)
        }
    }

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }

    // Метод для отладки - получение всех пользователей из БД
    suspend fun debugGetAllUsers(): List<UserEntity> {
        return try {
            authRepository.getAllUsers()
        } catch (e: Exception) {
            emptyList()
        }
    }
}