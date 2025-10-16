package com.example.uni_project.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND authProvider = 'LOCAL'")
    suspend fun getUserByCredentials(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND authProvider = 'GOOGLE'")
    suspend fun getGoogleUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)


    @Query("SELECT * FROM users WHERE token = :token")
    suspend fun getUserByToken(token: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastRegisteredUser(): UserEntity?

    @Query("SELECT * FROM users WHERE registrationCompleted = :completed")
    suspend fun getUsersByRegistrationStatus(completed: Boolean): List<UserEntity>

    @Query("SELECT * FROM users WHERE documentsUploaded = :uploaded")
    suspend fun getUsersByDocumentsStatus(uploaded: Boolean): List<UserEntity>


    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET token = :newToken WHERE email = :email")
    suspend fun updateUserToken(email: String, newToken: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Query("SELECT * FROM users WHERE registrationStep < 3")
    suspend fun getIncompleteRegistrations(): List<UserEntity>

    @Query("DELETE FROM users WHERE registrationStep < 3 AND createdAt < :timeThreshold")
    suspend fun cleanupIncompleteRegistrations(timeThreshold: Long = System.currentTimeMillis() - 24 * 60 * 60 * 1000): Int

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUserByEmail(email: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailExists(email: String): Int

    @Query("UPDATE users SET lastLogin = :timestamp WHERE email = :email")
    suspend fun updateLastLogin(email: String, timestamp: Long)

    @Query("UPDATE users SET registrationStep = :step WHERE email = :email")
    suspend fun updateRegistrationStep(email: String, step: Int)
}