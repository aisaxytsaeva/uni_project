package com.example.uni_project.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uni_project.core.data_class.Gender

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val password: String? = null,
    val token: String,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val gender: Gender = Gender.UNSPECIFIED,

    val profilePhotoUri: String? = null,
    val driverLicenseNumber: String = "",
    val driverLicenseIssueDate: String = "",
    val driverLicensePhotoUri: String? = null,
    val passportPhotoUri: String? = null,

    val authProvider: AuthProvider = AuthProvider.LOCAL,
    val googleId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val registrationStep: Int = 0,
    val registrationCompleted: Boolean = false,
    val documentsUploaded: Boolean = false
) {
    fun toUser(): User {
        return User(
            email = email,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender,
            profilePhotoUri = profilePhotoUri,
            driverLicenseNumber = driverLicenseNumber,
            driverLicenseIssueDate = driverLicenseIssueDate,
            driverLicensePhotoUri = driverLicensePhotoUri,
            passportPhotoUri = passportPhotoUri,
            authProvider = authProvider,
            createdAt = createdAt,
            lastLogin = lastLogin,
            registrationStep = registrationStep,
            registrationCompleted = registrationCompleted,
            documentsUploaded = documentsUploaded
        )
    }
}

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val birthDate: String,
    val gender: Gender,
    val profilePhotoUri: String?,
    val driverLicenseNumber: String,
    val driverLicenseIssueDate: String,
    val driverLicensePhotoUri: String?,
    val passportPhotoUri: String?,
    val authProvider: AuthProvider,
    val createdAt: Long,
    val lastLogin: Long, // ДОБАВИТЬ
    val registrationStep: Int, // ДОБАВИТЬ
    val registrationCompleted: Boolean,
    val documentsUploaded: Boolean
)

enum class AuthProvider {
    LOCAL,    // Обычная регистрация
    GOOGLE    // Через Google
}

enum class Gender {
    MALE,
    FEMALE,
    NOT_SELECTED
}