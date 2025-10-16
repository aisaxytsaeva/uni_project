package com.example.uni_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.data_class.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
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
                _state.value.birthDate.length == 8 && // 8 цифр для DDMMYYYY
                _state.value.gender != Gender.UNSPECIFIED &&
                isBirthDateValid(_state.value.birthDate)

    // Упрощенная валидация даты рождения (DDMMYYYY)
    private fun isBirthDateValid(date: String): Boolean {
        if (date.length != 8) return false

        return try {
            val day = date.substring(0, 2).toInt()
            val month = date.substring(2, 4).toInt()
            val year = date.substring(4, 8).toInt()


            day in 1..31 && month in 1..12 && year in 1900..2025
        } catch (e: Exception) {
            false
        }
    }

    fun updateLastName(lastName: String) {
        _state.update { currentState ->
            currentState.copy(
                lastName = lastName.trim(),
                lastNameError = null
            )
        }
    }

    fun updateFirstName(firstName: String) {
        _state.update { currentState ->
            currentState.copy(
                firstName = firstName.trim(),
                firstNameError = null
            )
        }
    }

    fun updateMiddleName(middleName: String) {
        _state.update { currentState ->
            currentState.copy(
                middleName = middleName.trim()
            )
        }
    }

    fun updateBirthDate(birthDate: String) {
        _state.update { currentState ->
            currentState.copy(
                birthDate = birthDate,
                birthDateError = null
            )
        }
    }

    fun updateGender(gender: Gender) {
        _state.update { currentState ->
            currentState.copy(
                gender = gender,
                genderError = null
            )
        }
    }

    fun clearErrors() {
        _state.update { currentState ->
            currentState.copy(
                lastNameError = null,
                firstNameError = null,
                birthDateError = null,
                genderError = null
            )
        }
    }

    suspend fun completeRegistration(email: String): Boolean {
        if (!isFormValid) {
            val errors = validateForm()
            _state.update { currentState ->
                currentState.copy(
                    lastNameError = errors["lastName"],
                    firstNameError = errors["firstName"],
                    birthDateError = errors["birthDate"],
                    genderError = errors["gender"]
                )
            }
            return false
        }

        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }

        return try {
            // Форматируем дату для отображения
            val displayDate = formatDateForDisplay(_state.value.birthDate)

            val result = authRepository.registerStep2(
                email = email,
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                middleName = _state.value.middleName,
                birthDate = _state.value.birthDate, // сохраняем как DDMMYYYY
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
                            "Дата рождения: $displayDate\n" +
                            "Пол: ${getGenderText(_state.value.gender)}"
                )
                true
            } else {
                _registrationResult.value = RegistrationResult.Error(
                    result.error ?: "Ошибка завершения регистрации"
                )
                false
            }
        } catch (e: Exception) {
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

        // Валидация фамилии
        when {
            _state.value.lastName.isBlank() -> {
                errors["lastName"] = "Фамилия обязательна для заполнения"
            }
            _state.value.lastName.length < 2 -> {
                errors["lastName"] = "Слишком короткая фамилия"
            }
        }

        // Валидация имени
        when {
            _state.value.firstName.isBlank() -> {
                errors["firstName"] = "Имя обязательно для заполнения"
            }
            _state.value.firstName.length < 2 -> {
                errors["firstName"] = "Слишком короткое имя"
            }
        }

        // Валидация даты рождения
        when {
            _state.value.birthDate.isBlank() -> {
                errors["birthDate"] = "Дата рождения обязательна для заполнения"
            }
            _state.value.birthDate.length != 8 -> {
                errors["birthDate"] = "Введите полную дату в формате ДД.ММ.ГГГГ"
            }
            !isBirthDateValid(_state.value.birthDate) -> {
                errors["birthDate"] = "Введите корректную дату рождения"
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
            Gender.UNSPECIFIED -> "Не выбран"
        }
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
}