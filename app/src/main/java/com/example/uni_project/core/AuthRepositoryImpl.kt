package com.example.uni_project.core

import android.app.Activity
import androidx.activity.result.ActivityResult
import android.content.Intent
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.dao.AuthProvider
import com.example.uni_project.dao.UserEntity
import com.example.uni_project.core.data_class.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.example.uni_project.core.data_class.Gender



class AuthRepositoryImpl(
    private val database: AppDatabase,
    private val googleAuthService: GoogleAuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    private var currentUserToken: String? = sessionManager.getAuthToken()
    private var _currentUser: UserEntity? = null

    // Аутентификация
    override suspend fun login(email: String, password: String): LoginResult {
        return try {
            println("🟡 DEBUG login: Attempting login for email: $email")
            val user = database.userDao().getUserByEmail(email)

            if (user != null && user.password == password.hashCode().toString()) {
                val newToken = generateToken(email)
                database.userDao().updateUserToken(email, newToken)
                currentUserToken = newToken
                sessionManager.saveAuthToken(newToken)
                _currentUser = user.copy(token = newToken)

                println(" DEBUG login: Login successful, token saved: $newToken")
                println(" DEBUG login: User details - email: ${user.email}, name: ${user.firstName} ${user.lastName}")

                LoginResult(success = true, token = newToken)
            } else {
                println("DEBUG login: Invalid credentials for email: $email")
                LoginResult(success = false, error = "Неверный email или пароль")
            }
        } catch (e: Exception) {
            println("DEBUG login: Error: ${e.message}")
            LoginResult(success = false, error = "Ошибка входа: ${e.message}")
        }
    }

    override suspend fun loginWithGoogle(account: GoogleSignInAccount): LoginResult {
        return try {
            println("DEBUG loginWithGoogle: Attempting Google login")
            val user = findOrCreateUserFromGoogleAccount(account)
            currentUserToken = user.token
            sessionManager.saveAuthToken(user.token)
            _currentUser = user

            println("🟢 DEBUG loginWithGoogle: Google login successful, token saved: ${user.token}")
            println("🟢 DEBUG loginWithGoogle: User details - email: ${user.email}, name: ${user.firstName} ${user.lastName}")

            LoginResult(success = true, token = user.token)
        } catch (e: Exception) {
            println("DEBUG loginWithGoogle: Error: ${e.message}")
            LoginResult(success = false, error = "Ошибка входа через Google: ${e.message}")
        }
    }

    // Трехэтапная регистрация
    override suspend fun registerStep1(email: String, password: String): LoginResult {
        return try {
            println("🟡 DEBUG registerStep1: Starting registration for: $email")


            val allUsersBefore = database.userDao().getAllUsers().map { it.email }
            println(" DEBUG registerStep1: Users before registration: $allUsersBefore")

            if (isEmailRegistered(email)) {
                println(" DEBUG registerStep1: Email already exists: $email")
                return LoginResult(success = false, error = "Пользователь с таким email уже существует")
            }

            val newToken = generateToken(email)
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
                token = newToken,
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                authProvider = AuthProvider.LOCAL,
                registrationStep = 1,
                registrationCompleted = false,
                documentsUploaded = false
            )

            database.userDao().insertUser(newUser)
            currentUserToken = newToken
            sessionManager.saveAuthToken(newToken)
            _currentUser = newUser

            val allUsersAfter = database.userDao().getAllUsers().map { it.email }
            println(" DEBUG registerStep1: Users after registration: $allUsersAfter")

            val createdUser = database.userDao().getUserByEmail(email)
            println(" DEBUG registerStep1: User created successfully: ${createdUser != null}")
            println("DEBUG registerStep1: Token saved to SharedPreferences: $newToken")

            LoginResult(success = true, token = newToken)
        } catch (e: Exception) {
            println(" DEBUG registerStep1: Error: ${e.message}")
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
            println(" DEBUG registerStep2: Searching for user with email: $email")

            val allUsers = database.userDao().getAllUsers()
            println(" DEBUG registerStep2: All users in DB: ${allUsers.map { it.email }}")

            val user = database.userDao().getUserByEmail(email)
            println(" DEBUG registerStep2: User found: ${user != null}")

            if (user != null) {
                println(" DEBUG registerStep2: User details - email: ${user.email}, step: ${user.registrationStep}")

                val updatedUser = user.copy(
                    firstName = firstName,
                    lastName = lastName,
                    middleName = middleName,
                    birthDate = birthDate,
                    gender = gender,
                    registrationStep = 2
                )
                database.userDao().updateUser(updatedUser)
                _currentUser = updatedUser
                println(" DEBUG registerStep2: User updated successfully")
                LoginResult(success = true, token = user.token)
            } else {
                println(" DEBUG registerStep2: User not found for email: $email")
                LoginResult(success = false, error = "Пользователь не найден")
            }
        } catch (e: Exception) {
            println(" DEBUG registerStep2: Error: ${e.message}")
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
            println("🟡 DEBUG registerStep3: Starting registration for: $email")

            val existingUser = database.userDao().getUserByEmail(email)
            if (existingUser == null) {
                println(" DEBUG registerStep3: User not found for email: $email")
                return LoginResult(success = false, error = "Пользователь не найден")
            }


            val token = generateToken(email)

            val updatedUser = existingUser.copy(
                profilePhotoUri = profilePhotoUri,
                driverLicenseNumber = driverLicenseNumber,
                driverLicenseIssueDate = driverLicenseIssueDate,
                driverLicensePhotoUri = driverLicensePhotoUri,
                passportPhotoUri = passportPhotoUri,
                token = token,
                registrationStep = 3,
                registrationCompleted = true,
                documentsUploaded = true,
                lastLogin = System.currentTimeMillis()
            )

            database.userDao().insertUser(updatedUser)
            _currentUser = updatedUser
            currentUserToken = token
            sessionManager.saveAuthToken(token)

            println(" DEBUG registerStep3: Registration COMPLETED for: $email")
            LoginResult(success = true, token = token)

        } catch (e: Exception) {
            println(" DEBUG registerStep3: Error: ${e.message}")
            LoginResult(success = false, error = e.message ?: "Неизвестная ошибка")
        }
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return try {
            database.userDao().getUserByEmail(email)
        } catch (e: Exception) {
            println(" DEBUG getUserByEmail: Error: ${e.message}")
            null
        }
    }

    override suspend fun getUserByToken(token: String): UserEntity? {
        return try {
            database.userDao().getUserByToken(token)
        } catch (e: Exception) {
            println("DEBUG getUserByToken: Error: ${e.message}")
            null
        }
    }

    override suspend fun getAllUsers(): List<UserEntity> {
        return try {
            database.userDao().getAllUsers()
        } catch (e: Exception) {
            println(" DEBUG getAllUsers: Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getLastRegisteredUser(): UserEntity? {
        return try {
            database.userDao().getLastRegisteredUser()
        } catch (e: Exception) {
            println(" DEBUG getLastRegisteredUser: Error: ${e.message}")
            null
        }
    }

    override suspend fun deleteUser(email: String): Boolean {
        return try {
            println("🟡 DEBUG deleteUser: Attempting to delete user: $email")

            val currentUser = getCurrentUser()
            val isCurrentUser = currentUser?.email == email

            val result = database.userDao().deleteUserByEmail(email)
            val isDeleted = result > 0

            if (isDeleted) {
                if (isCurrentUser) {
                    currentUserToken = null
                    _currentUser = null
                    sessionManager.clearAuthToken()
                    println(" DEBUG deleteUser: Current user deleted, token cleared")
                }
                println(" DEBUG deleteUser: User deleted successfully: $email")
            } else {
                println(" DEBUG deleteUser: Failed to delete user: $email")
            }

            isDeleted
        } catch (e: Exception) {
            println(" DEBUG deleteUser: Error: ${e.message}")
            false
        }
    }

    override suspend fun updateUser(user: UserEntity): Boolean {
        return try {
            database.userDao().updateUser(user)
            if (_currentUser?.email == user.email) {
                _currentUser = user
            }
            true
        } catch (e: Exception) {
            println(" DEBUG updateUser: Error: ${e.message}")
            false
        }
    }

    override suspend fun updateUserToken(email: String, newToken: String): Boolean {
        return try {
            database.userDao().updateUserToken(email, newToken)
            if (_currentUser?.email == email) {
                _currentUser = _currentUser?.copy(token = newToken)
                currentUserToken = newToken
                sessionManager.saveAuthToken(newToken)
            }
            true
        } catch (e: Exception) {
            println(" DEBUG updateUserToken: Error: ${e.message}")
            false
        }
    }

    override suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            database.userDao().getUserByEmail(email) != null
        } catch (e: Exception) {
            println(" DEBUG isEmailRegistered: Error: ${e.message}")
            false
        }
    }

    override suspend fun getUsersCount(): Int {
        return try {
            database.userDao().getUsersCount()
        } catch (e: Exception) {
            println(" DEBUG getUsersCount: Error: ${e.message}")
            0
        }
    }

    // Управление сессией
    override suspend fun getCurrentUser(): UserEntity? {
        return try {

            if (_currentUser != null) {
                println(" DEBUG getCurrentUser: User found in memory: ${_currentUser?.email}")
                return _currentUser
            }


            currentUserToken?.let { token ->
                val user = database.userDao().getUserByToken(token)
                if (user != null) {
                    _currentUser = user
                    println(" DEBUG getCurrentUser: User found by memory token: ${user.email}")
                    return user
                }
            }


            sessionManager.getAuthToken()?.let { token ->
                val user = database.userDao().getUserByToken(token)
                if (user != null) {
                    currentUserToken = token
                    _currentUser = user
                    println(" DEBUG getCurrentUser: User found in SharedPreferences: ${user.email}")
                    return user
                } else {
                    // Токен в SharedPreferences есть, но пользователь не найден в БД
                    sessionManager.clearAuthToken()
                    println(" DEBUG getCurrentUser: Token in SharedPreferences but user not found in DB, clearing token")
                }
            }

            println(" DEBUG getCurrentUser: No current user found")
            null
        } catch (e: Exception) {
            println("DEBUG getCurrentUser: Error: ${e.message}")
            null
        }
    }

    override suspend fun logout() {
        try {
            println(" DEBUG logout: Logging out user")
            currentUserToken = null
            _currentUser = null
            sessionManager.clearAuthToken()
            println(" DEBUG logout: User logged out successfully")
        } catch (e: Exception) {
            println(" DEBUG logout: Error: ${e.message}")
        }
    }

    override suspend fun getIncompleteRegistrations(): List<UserEntity> {
        return try {
            database.userDao().getIncompleteRegistrations()
        } catch (e: Exception) {
            println(" DEBUG getIncompleteRegistrations: Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun cleanupIncompleteRegistrations(): Int {
        return try {
            database.userDao().cleanupIncompleteRegistrations()
        } catch (e: Exception) {
            println(" DEBUG cleanupIncompleteRegistrations: Error: ${e.message}")
            0
        }
    }

    // Google Sign-In
    override suspend fun getGoogleSignInIntent(): Intent {
        return googleAuthService.getSignInIntent()
    }

    override suspend fun handleGoogleSignInResult(result: ActivityResult): LoginResult {
        return try {
            println(" DEBUG handleGoogleSignInResult: Processing Google sign-in result")

            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val signInResult = googleAuthService.signInWithIntent(data)

                    when (signInResult) {
                        is GoogleSignInResult.Success -> {
                            println(" DEBUG handleGoogleSignInResult: Google sign-in successful")
                            loginWithGoogle(signInResult.account)
                        }
                        is GoogleSignInResult.Error -> {
                            println(" DEBUG handleGoogleSignInResult: Google sign-in error: ${signInResult.message}")
                            LoginResult(
                                success = false,
                                error = "Ошибка Google Sign-In: ${signInResult.message}"
                            )
                        }
                    }
                } else {
                    println(" DEBUG handleGoogleSignInResult: No data received")
                    LoginResult(success = false, error = "Данные не получены")
                }
            } else {
                println(" DEBUG handleGoogleSignInResult: Result not OK: ${result.resultCode}")
                LoginResult(success = false, error = "Авторизация отменена")
            }
        } catch (e: Exception) {
            println(" DEBUG handleGoogleSignInResult: Error: ${e.message}")
            LoginResult(success = false, error = "Ошибка обработки: ${e.message}")
        }
    }

    private suspend fun findOrCreateUserFromGoogleAccount(account: GoogleSignInAccount): UserEntity {
        val userEmail = account.email ?: throw IllegalArgumentException("Email is required")
        println(" DEBUG findOrCreateUserFromGoogleAccount: Processing Google account: $userEmail")

        val existingUser = getUserByEmail(userEmail)

        return if (existingUser != null) {
            println("DEBUG findOrCreateUserFromGoogleAccount: User exists, updating: $userEmail")

            val newToken = generateToken(userEmail)
            val updatedUser = existingUser.copy(
                token = newToken,
                lastLogin = System.currentTimeMillis(),
                firstName = account.givenName ?: existingUser.firstName,
                lastName = account.familyName ?: existingUser.lastName,
                authProvider = AuthProvider.GOOGLE,
                registrationStep = 3,
                registrationCompleted = true
            )
            updateUser(updatedUser)
            println(" DEBUG findOrCreateUserFromGoogleAccount: User updated: $userEmail")
            updatedUser
        } else {
            println(" DEBUG findOrCreateUserFromGoogleAccount: Creating new user: $userEmail")

            val newUser = UserEntity(
                email = userEmail,
                password = null,
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
                token = generateToken(userEmail),
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                authProvider = AuthProvider.GOOGLE,
                registrationStep = 3,
                registrationCompleted = true,
                documentsUploaded = false
            )
            insertUser(newUser)
            println(" DEBUG findOrCreateUserFromGoogleAccount: New user created: $userEmail")
            newUser
        }
    }

    private fun generateToken(email: String): String {
        return "auth_token_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private suspend fun insertUser(user: UserEntity) {
        try {
            database.userDao().insertUser(user)
        } catch (e: Exception) {
            println(" DEBUG insertUser: Error: ${e.message}")
            throw e
        }
    }
}