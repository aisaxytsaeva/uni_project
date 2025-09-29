package com.example.uni_project.presentation.viewmodel



import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.uni_project.core.data_class.AuthRepository
import com.example.uni_project.core.data_class.DocumentUploadState
import com.example.uni_project.core.data_class.RegistrationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentUploadViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DocumentUploadState())
    val state: StateFlow<DocumentUploadState> = _state.asStateFlow()

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult.asStateFlow()

    // Проверка готовности формы
    val isFormValid: Boolean
        get() = _state.value.driverLicenseNumber.isNotBlank() &&
                _state.value.driverLicenseIssueDate.isNotBlank() &&
                _state.value.driverLicensePhotoUri != null &&
                _state.value.passportPhotoUri != null &&
                isIssueDateValid(_state.value.driverLicenseIssueDate)

    // Валидация даты выдачи (DD/MM/YYYY)
    private fun isIssueDateValid(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            dateFormat.isLenient = false
            val parsedDate = dateFormat.parse(date)
            parsedDate != null && parsedDate.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun updateProfilePhoto(uri: String?) {
        _state.value = _state.value.copy(profilePhotoUri = uri)
    }

    fun updateDriverLicenseNumber(number: String) {
        _state.value = _state.value.copy(
            driverLicenseNumber = number.trim(),
            driverLicenseError = null
        )
    }

    fun updateDriverLicenseIssueDate(date: String) {
        _state.value = _state.value.copy(
            driverLicenseIssueDate = date,
            issueDateError = null
        )
    }

    fun updateDriverLicensePhoto(uri: String?) {
        _state.value = _state.value.copy(
            driverLicensePhotoUri = uri,
            driverLicensePhotoError = null
        )
    }

    fun updatePassportPhoto(uri: String?) {
        _state.value = _state.value.copy(
            passportPhotoUri = uri,
            passportPhotoError = null
        )
    }

    fun clearErrors() {
        _state.value = _state.value.copy(
            driverLicenseError = null,
            issueDateError = null,
            driverLicensePhotoError = null,
            passportPhotoError = null
        )
    }

    suspend fun completeDocumentUpload(email: String): Boolean {
        if (!isFormValid) {
            val errors = validateForm()
            _state.value = _state.value.copy(
                driverLicenseError = errors["driverLicense"],
                issueDateError = errors["issueDate"],
                driverLicensePhotoError = errors["driverLicensePhoto"],
                passportPhotoError = errors["passportPhoto"]
            )
            return false
        }

        _state.value = _state.value.copy(isLoading = true)

        return try {
            val result = authRepository.registerStep3(
                email = email,
                profilePhotoUri = _state.value.profilePhotoUri,
                driverLicenseNumber = _state.value.driverLicenseNumber,
                driverLicenseIssueDate = _state.value.driverLicenseIssueDate,
                driverLicensePhotoUri = _state.value.driverLicensePhotoUri,
                passportPhotoUri = _state.value.passportPhotoUri
            )

            if (result.success) {
                _registrationResult.value = RegistrationResult.Success(
                    message = "Регистрация завершена успешно!\n\n" +
                            "Все документы сохранены в базе данных.\n" +
                            "Номер водительского удостоверения: ${_state.value.driverLicenseNumber}\n" +
                            "Дата выдачи: ${_state.value.driverLicenseIssueDate}"
                )
                true
            } else {
                _registrationResult.value = RegistrationResult.Error(result.error ?: "Ошибка загрузки документов")
                false
            }
        } catch (e: Exception) {
            _registrationResult.value = RegistrationResult.Error("Ошибка сети: ${e.message}")
            false
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun validateForm(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        // Валидация номера водительского удостоверения
        if (_state.value.driverLicenseNumber.isBlank()) {
            errors["driverLicense"] = "Номер водительского удостоверения обязателен"
        }

        // Валидация даты выдачи
        when {
            _state.value.driverLicenseIssueDate.isBlank() -> {
                errors["issueDate"] = "Дата выдачи обязательна"
            }
            !isIssueDateValid(_state.value.driverLicenseIssueDate) -> {
                errors["issueDate"] = "Введите корректную дату выдачи в формате DD/MM/YYYY"
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

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }
}