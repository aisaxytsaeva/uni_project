package com.example.uni_project.presentation.viewmodel



import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.SessionManager
import com.example.uni_project.core.data_class.DocumentUploadState
import com.example.uni_project.core.data_class.RegistrationResult
import kotlinx.coroutines.flow.update

class DocumentUploadViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(DocumentUploadState())
    val state: StateFlow<DocumentUploadState> = _state.asStateFlow()

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult.asStateFlow()

    // Проверка готовности формы
    val isFormValid: Boolean
        get() = _state.value.driverLicenseNumber.isNotBlank() &&
                _state.value.driverLicenseIssueDate.length == 8 && // 8 цифр для DDMMYYYY
                _state.value.driverLicensePhotoUri != null &&
                _state.value.passportPhotoUri != null &&
                isIssueDateValid(_state.value.driverLicenseIssueDate)

    private fun isIssueDateValid(date: String): Boolean {
        if (date.length != 8) return false

        return try {
            val day = date.substring(0, 2).toInt()
            val month = date.substring(2, 4).toInt()
            val year = date.substring(4, 8).toInt()

            // Базовая проверка
            day in 1..31 && month in 1..12 && year in 1900..2025
        } catch (e: Exception) {
            false
        }
    }

    fun updateProfilePhoto(uri: String?) {
        _state.update { currentState ->
            currentState.copy(profilePhotoUri = uri)
        }
    }

    fun updateDriverLicenseNumber(number: String) {
        _state.update { currentState ->
            currentState.copy(
                driverLicenseNumber = number.trim(),
                driverLicenseError = null
            )
        }
    }

    fun updateDriverLicenseIssueDate(date: String) {
        val filtered = date.filter { it.isDigit() }

        _state.update { currentState ->
            currentState.copy(
                driverLicenseIssueDate = filtered,
                issueDateError = null
            )
        }

        // Валидация при полном вводе
        if (filtered.length == 8) {
            val day = filtered.substring(0, 2).toIntOrNull()
            val month = filtered.substring(2, 4).toIntOrNull()
            val year = filtered.substring(4, 8).toIntOrNull()

            val error = when {
                day == null || day !in 1..31 -> "Некорректный день"
                month == null || month !in 1..12 -> "Некорректный месяц"
                year == null || year < 1900 || year > 2025 -> "Некорректный год"
                else -> null
            }

            _state.update { currentState ->
                currentState.copy(issueDateError = error)
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    issueDateError = if (filtered.isNotEmpty()) "Введите полную дату" else null
                )
            }
        }
    }

    fun updateDriverLicensePhoto(uri: String?) {
        _state.update { currentState ->
            currentState.copy(
                driverLicensePhotoUri = uri,
                driverLicensePhotoError = null
            )
        }
    }

    fun updatePassportPhoto(uri: String?) {
        _state.update { currentState ->
            currentState.copy(
                passportPhotoUri = uri,
                passportPhotoError = null
            )
        }
    }

    fun clearErrors() {
        _state.update { currentState ->
            currentState.copy(
                driverLicenseError = null,
                issueDateError = null,
                driverLicensePhotoError = null,
                passportPhotoError = null
            )
        }
    }


    suspend fun completeRegistration(
        email: String,

    ): Boolean {
        if (!isFormValid) {
            val errors = validateForm()
            _state.update { currentState ->
                currentState.copy(
                    driverLicenseError = errors["driverLicense"],
                    issueDateError = errors["issueDate"],
                    driverLicensePhotoError = errors["driverLicensePhoto"],
                    passportPhotoError = errors["passportPhoto"]
                )
            }
            return false
        }

        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }

        return try {
            println(" DEBUG completeRegistration: Starting registration for: $email")
            println(" DEBUG completeRegistration: Driver license: ${_state.value.driverLicenseNumber}")


            val result = authRepository.registerStep3(
                email = email,
                profilePhotoUri = _state.value.profilePhotoUri,
                driverLicenseNumber = _state.value.driverLicenseNumber,
                driverLicenseIssueDate = _state.value.driverLicenseIssueDate,
                driverLicensePhotoUri = _state.value.driverLicensePhotoUri,
                passportPhotoUri = _state.value.passportPhotoUri
            )

            if (result.success && result.token != null) {

                sessionManager.saveAuthData(result.token, email)

                println(" DEBUG completeRegistration: Registration SUCCESSFUL for: $email")
                println(" DEBUG completeRegistration: Token saved: ${result.token}")

                _registrationResult.value = RegistrationResult.Success(
                    message = "Регистрация завершена успешно!\n\n"
                )
                true
            } else {
                println(" DEBUG completeRegistration: Registration FAILED: ${result.error}")
                _registrationResult.value = RegistrationResult.Error(
                    result.error ?: "Ошибка завершения регистрации"
                )
                false
            }
        } catch (e: Exception) {
            println("DEBUG completeRegistration: Exception: ${e.message}")
            _registrationResult.value = RegistrationResult.Error(
                "Ошибка: ${e.message ?: "Неизвестная ошибка"}"
            )
            false
        } finally {
            _state.update { currentState ->
                currentState.copy(isLoading = false)
            }
        }
    }

    private fun validateForm(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        // Валидация номера водительского удостоверения
        when {
            _state.value.driverLicenseNumber.isBlank() -> {
                errors["driverLicense"] = "Номер водительского удостоверения обязателен"
            }
            _state.value.driverLicenseNumber.length < 2 -> {
                errors["driverLicense"] = "Слишком короткий номер"
            }
            !_state.value.driverLicenseNumber.all { it.isLetterOrDigit() } -> {
                errors["driverLicense"] = "Номер должен содержать только буквы и цифры"
            }
        }

        // Валидация даты выдачи
        when {
            _state.value.driverLicenseIssueDate.isBlank() -> {
                errors["issueDate"] = "Дата выдачи обязательна"
            }
            _state.value.driverLicenseIssueDate.length != 8 -> {
                errors["issueDate"] = "Введите полную дату в формате ДД.ММ.ГГГГ"
            }
            !isIssueDateValid(_state.value.driverLicenseIssueDate) -> {
                errors["issueDate"] = "Введите корректную дату выдачи"
            }
        }

        // Валидация фото документов
        if (_state.value.driverLicensePhotoUri == null) {
            errors["driverLicensePhoto"] = "Загрузите фото водительского удостоверения"
        }

        if (_state.value.passportPhotoUri == null) {
            errors["passportPhoto"] = "Загрузите фото паспорта"
        }

        return errors
    }

    // Функция для форматирования даты для отображения
    private fun formatDateForDisplay(date: String): String {
        return if (date.length == 8) {
            "${date.substring(0, 2)}.${date.substring(2, 4)}.${date.substring(4, 8)}"
        } else {
            date
        }
    }

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }

    // Дополнительный метод для проверки состояния
    fun getFormValidationStatus(): String {
        return """
            Form Validation Status:
            - Driver License: ${_state.value.driverLicenseNumber.isNotBlank()} (${_state.value.driverLicenseNumber})
            - Issue Date: ${_state.value.driverLicenseIssueDate.length == 8} (${_state.value.driverLicenseIssueDate})
            - License Photo: ${_state.value.driverLicensePhotoUri != null}
            - Passport Photo: ${_state.value.passportPhotoUri != null}
            - Overall Valid: $isFormValid
        """.trimIndent()
    }
}