package com.example.uni_project.core

import android.app.Activity
import androidx.activity.result.ActivityResult
import android.content.Intent
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.dao.AuthProvider
import com.example.uni_project.dao.UserEntity
import com.example.uni_project.core.data_class.AuthRepository
import com.example.uni_project.core.data_class.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.example.uni_project.core.data_class.Gender


class AuthRepositoryImpl(
    private val database: AppDatabase,
    private val googleAuthService: GoogleAuthService,

) : AuthRepository {

    private var currentUserToken: String? = null

    // Аутентификация
    override suspend fun login(email: String, password: String): LoginResult {
        return try {
            val user = database.userDao().getUserByEmail(email)
            if (user != null && user.password == password.hashCode().toString()) {
                // Генерируем новый токен при входе
                val newToken = generateToken()
                database.userDao().updateUserToken(email, newToken)
                currentUserToken = newToken
                LoginResult(success = true, token = newToken)
            } else {
                LoginResult(success = false, error = "Неверный email или пароль")
            }
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка входа: ${e.message}")
        }
    }

    override suspend fun loginWithGoogle(account: GoogleSignInAccount): LoginResult {
        return try {
            val user = findOrCreateUserFromGoogleAccount(account)
            currentUserToken = user.token
            LoginResult(success = true, token = user.token)
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка входа через Google: ${e.message}")
        }
    }

    // Трехэтапная регистрация
    override suspend fun registerStep1(email: String, password: String): LoginResult {
        return try {
            // Проверяем, не зарегистрирован ли уже email
            if (isEmailRegistered(email)) {
                return LoginResult(success = false, error = "Пользователь с таким email уже существует")
            }

            // Создаем пользователя с первым шагом регистрации
            val newUser = UserEntity(
                email = email,
                password = password.hashCode().toString(),
                firstName = "",
                lastName = "",
                middleName = "",
                birthDate = "",
                gender = Gender.UNSPECIFIED,
                profilePhotoUri = null,
                driverLicenseNumber = "",
                driverLicenseIssueDate = "",
                driverLicensePhotoUri = null,
                passportPhotoUri = null,
                token = generateToken(),
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                authProvider = AuthProvider.LOCAL,
                registrationStep = 1
            )

            database.userDao().insertUser(newUser)
            currentUserToken = newUser.token
            LoginResult(success = true, token = newUser.token)
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка регистрации: ${e.message}")
        }
    }

    override suspend fun registerStep2(
        email: String,
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: Gender
    ): LoginResult {
        return try {
            val user = database.userDao().getUserByEmail(email)
            if (user != null) {
                val updatedUser = user.copy(
                    firstName = firstName,
                    lastName = lastName,
                    middleName = middleName,
                    birthDate = birthDate,
                    gender = gender,
                    registrationStep = 2
                )
                database.userDao().updateUser(updatedUser)
                LoginResult(success = true, token = user.token)
            } else {
                LoginResult(success = false, error = "Пользователь не найден")
            }
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка сохранения данных: ${e.message}")
        }
    }

    override suspend fun registerStep3(
        email: String,
        profilePhotoUri: String?,
        driverLicenseNumber: String,
        driverLicenseIssueDate: String,
        driverLicensePhotoUri: String?,
        passportPhotoUri: String?
    ): LoginResult {
        return try {
            val user = database.userDao().getUserByEmail(email)
            if (user != null) {
                val updatedUser = user.copy(
                    profilePhotoUri = profilePhotoUri,
                    driverLicenseNumber = driverLicenseNumber,
                    driverLicenseIssueDate = driverLicenseIssueDate,
                    driverLicensePhotoUri = driverLicensePhotoUri,
                    passportPhotoUri = passportPhotoUri,
                    registrationStep = 3 // Регистрация завершена
                )
                database.userDao().updateUser(updatedUser)
                LoginResult(success = true, token = user.token)
            } else {
                LoginResult(success = false, error = "Пользователь не найден")
            }
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка сохранения документов: ${e.message}")
        }
    }

    // Управление пользователями
    override suspend fun getUserByEmail(email: String): UserEntity? {
        return database.userDao().getUserByEmail(email)
    }

    override suspend fun getUserByToken(token: String): UserEntity? {
        return database.userDao().getUserByToken(token)
    }

    override suspend fun getAllUsers(): List<UserEntity> {
        return database.userDao().getAllUsers()
    }

    override suspend fun getLastRegisteredUser(): UserEntity? {
        return database.userDao().getLastRegisteredUser()
    }

    override suspend fun deleteUser(email: String): Boolean {
        return try {
            database.userDao().deleteUserByEmail(email)
            if (currentUserToken == database.userDao().getUserByEmail(email)?.token) {
                currentUserToken = null
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUser(user: UserEntity): Boolean {
        return try {
            database.userDao().updateUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUserToken(email: String, newToken: String): Boolean {
        return try {
            database.userDao().updateUserToken(email, newToken)
            if (currentUserToken == database.userDao().getUserByEmail(email)?.token) {
                currentUserToken = newToken
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isEmailRegistered(email: String): Boolean {
        return database.userDao().getUserByEmail(email) != null
    }

    override suspend fun getUsersCount(): Int {
        return database.userDao().getUsersCount()
    }

    // Управление сессией
    override suspend fun getCurrentUser(): UserEntity? {
        return currentUserToken?.let { database.userDao().getUserByToken(it) }
    }

    override suspend fun logout() {
        currentUserToken = null
    }

    override suspend fun getIncompleteRegistrations(): List<UserEntity> {
        return database.userDao().getIncompleteRegistrations()
    }

    override suspend fun cleanupIncompleteRegistrations(): Int {
        return database.userDao().cleanupIncompleteRegistrations()
    }

    // Google Sign-In
    override suspend fun getGoogleSignInIntent(): Intent {
        return googleAuthService.getSignInIntent()
    }

    override suspend fun handleGoogleSignInResult(result: ActivityResult): LoginResult {
        return try {
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val signInResult = googleAuthService.signInWithIntent(data)

                    when (signInResult) {
                        is GoogleSignInResult.Success -> {
                            loginWithGoogle(signInResult.account)
                        }
                        is GoogleSignInResult.Error -> {
                            LoginResult(
                                success = false,
                                error = "Ошибка Google Sign-In: ${signInResult.message}"
                            )
                        }
                    }
                } else {
                    LoginResult(success = false, error = "Данные не получены")
                }
            } else {
                LoginResult(success = false, error = "Авторизация отменена")
            }
        } catch (e: Exception) {
            LoginResult(success = false, error = "Ошибка обработки: ${e.message}")
        }
    }


    private suspend fun findOrCreateUserFromGoogleAccount(account: GoogleSignInAccount): UserEntity {
        val userEmail = account.email ?: throw IllegalArgumentException("Email is required")

        // Ищем пользователя в базе данных по email
        val existingUser = getUserByEmail(userEmail)

        return if (existingUser != null) {
            // Пользователь существует - обновляем токен и время входа
            val newToken = generateToken()
            val updatedUser = existingUser.copy(
                token = newToken,
                lastLogin = System.currentTimeMillis(),
                firstName = account.givenName ?: existingUser.firstName,
                lastName = account.familyName ?: existingUser.lastName,
                authProvider = AuthProvider.GOOGLE
            )
            updateUser(updatedUser)
            updatedUser
        } else {
            // Создаем нового пользователя
            val newUser = UserEntity(
                email = userEmail,
                password = "", // Пароль не нужен для Google аутентификации
                firstName = account.givenName ?: "",
                lastName = account.familyName ?: "",
                middleName = "",
                birthDate = "",
                gender = Gender.UNSPECIFIED,
                profilePhotoUri = account.photoUrl?.toString(),
                driverLicenseNumber = "",
                driverLicenseIssueDate = "",
                driverLicensePhotoUri = null,
                passportPhotoUri = null,
                token = generateToken(),
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                authProvider = AuthProvider.GOOGLE,
                registrationStep = 3 // Пропускаем шаги регистрации для Google
            )
            insertUser(newUser)
            newUser
        }
    }

    private fun generateToken(): String {
        return "auth_token_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private suspend fun insertUser(user: UserEntity) {
        database.userDao().insertUser(user)
    }
}