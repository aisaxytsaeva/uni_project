package com.example.uni_project.core.data_class

data class RegistrationDetailsState(
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val gender: Gender = Gender.NOT_SELECTED,
    val isLoading: Boolean = false,
    val lastNameError: String? = null,
    val firstNameError: String? = null,
    val birthDateError: String? = null,
    val genderError: String? = null
)

