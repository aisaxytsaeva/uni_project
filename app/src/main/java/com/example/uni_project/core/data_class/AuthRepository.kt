package com.example.uni_project.core.data_class



import com.example.uni_project.dao.UserEntity

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Intent
import androidx.activity.result.ActivityResult


interface AuthRepository {
    // Аутентификация
    suspend fun login(email: String, password: String): LoginResult
    suspend fun loginWithGoogle(account: GoogleSignInAccount): LoginResult

    // Трехэтапная регистрация
    suspend fun registerStep1(email: String, password: String): LoginResult
    suspend fun registerStep2(
        email: String,
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: Gender
    ): LoginResult
    suspend fun registerStep3(
        email: String,
        profilePhotoUri: String?,
        driverLicenseNumber: String,
        driverLicenseIssueDate: String,
        driverLicensePhotoUri: String?,
        passportPhotoUri: String?
    ): LoginResult

    // Управление пользователями
    suspend fun getUserByEmail(email: String): UserEntity?
    suspend fun getUserByToken(token: String): UserEntity?
    suspend fun getAllUsers(): List<UserEntity>
    suspend fun getLastRegisteredUser(): UserEntity?
    suspend fun deleteUser(email: String): Boolean
    suspend fun updateUser(user: UserEntity): Boolean
    suspend fun updateUserToken(email: String, newToken: String): Boolean
    suspend fun isEmailRegistered(email: String): Boolean
    suspend fun getUsersCount(): Int

    // Управление сессией
    suspend fun getCurrentUser(): UserEntity?
    suspend fun logout()

    suspend fun getIncompleteRegistrations(): List<UserEntity>
    suspend fun cleanupIncompleteRegistrations(): Int

    // Google Sign-In - ОБНОВЛЕННЫЕ МЕТОДЫ
    suspend fun getGoogleSignInIntent(): Intent
    suspend fun handleGoogleSignInResult(result: ActivityResult): LoginResult
}