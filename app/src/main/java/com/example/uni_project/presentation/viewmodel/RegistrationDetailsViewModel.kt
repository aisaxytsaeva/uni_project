package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uni_project.core.data_class.AuthRepository
import com.example.uni_project.core.data_class.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.uni_project.core.data_class.RegistrationDetailsState
import com.example.uni_project.core.data_class.RegistrationResult

class RegistrationDetailsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationDetailsState())
    val state: StateFlow<RegistrationDetailsState> = _state.asStateFlow()

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult.asStateFlow()

    // Проверка готовности формы
    val isFormValid: Boolean
        get() = _state.value.lastName.isNotBlank() &&
                _state.value.firstName.isNotBlank() &&
                _state.value.birthDate.isNotBlank() &&
                _state.value.gender != Gender.UNSPECIFIED &&
                isBirthDateValid(_state.value.birthDate)

    // Валидация даты рождения (MM/DD/YYYY)
    private fun isBirthDateValid(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            dateFormat.isLenient = false
            val parsedDate = dateFormat.parse(date)
            parsedDate != null && parsedDate.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun updateLastName(lastName: String) {
        _state.value = _state.value.copy(
            lastName = lastName.trim(),
            lastNameError = null
        )
    }

    fun updateFirstName(firstName: String) {
        _state.value = _state.value.copy(
            firstName = firstName.trim(),
            firstNameError = null
        )
    }

    fun updateMiddleName(middleName: String) {
        _state.value = _state.value.copy(
            middleName = middleName.trim()
        )
    }

    fun updateBirthDate(birthDate: String) {
        _state.value = _state.value.copy(
            birthDate = birthDate,
            birthDateError = null
        )
    }

    fun updateGender(gender: Gender) {
        _state.value = _state.value.copy(
            gender = gender,
            genderError = null
        )
    }

    fun clearErrors() {
        _state.value = _state.value.copy(
            lastNameError = null,
            firstNameError = null,
            birthDateError = null,
            genderError = null
        )
    }




    suspend fun completeRegistration(email: String): Boolean {
        if (!isFormValid) {
            val errors = validateForm()
            _state.value = _state.value.copy(
                lastNameError = errors["lastName"],
                firstNameError = errors["firstName"],
                birthDateError = errors["birthDate"],
                genderError = errors["gender"]
            )
            return false
        }

        _state.value = _state.value.copy(isLoading = true)

        return try {
            // Используем authRepository переданный в конструкторе
            val result = authRepository.registerStep2(
                email = email,
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                middleName = _state.value.middleName,
                birthDate = _state.value.birthDate,
                gender = _state.value.gender
            )

            if (result.success) {
                val fullName = buildString {
                    append(_state.value.lastName)
                    append(" ")
                    append(_state.value.firstName)
                    if (_state.value.middleName.isNotBlank()) {
                        append(" ")
                        append(_state.value.middleName)
                    }
                }

                _registrationResult.value = RegistrationResult.Success(
                    message = "Регистрация завершена успешно!\n\n" +
                            "Данные пользователя:\n" +
                            "ФИО: $fullName\n" +
                            "Дата рождения: ${_state.value.birthDate}\n" +
                            "Пол: ${getGenderText(_state.value.gender)}\n\n" +
                            "Все данные сохранены в базе данных!"
                )
                true
            } else {
                _registrationResult.value = RegistrationResult.Error(result.error ?: "Ошибка завершения регистрации")
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

        // Валидация фамилии
        if (_state.value.lastName.isBlank()) {
            errors["lastName"] = "Фамилия обязательна для заполнения"
        }

        // Валидация имени
        if (_state.value.firstName.isBlank()) {
            errors["firstName"] = "Имя обязательно для заполнения"
        }

        // Валидация даты рождения
        when {
            _state.value.birthDate.isBlank() -> {
                errors["birthDate"] = "Дата рождения обязательна для заполнения"
            }
            !isBirthDateValid(_state.value.birthDate) -> {
                errors["birthDate"] = "Введите корректную дату рождения в формате MM/DD/YYYY"
            }
        }

        // Валидация пола
        if (_state.value.gender == Gender.UNSPECIFIED) {
            errors["gender"] = "Выберите пол"
        }

        return errors
    }

    private fun getGenderText(gender: Gender): String {
        return when (gender) {
            Gender.MALE -> "Мужской"
            Gender.FEMALE -> "Женский"
            Gender.UNSPECIFIED, -> "Не выбран"
        }
    }

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }
}