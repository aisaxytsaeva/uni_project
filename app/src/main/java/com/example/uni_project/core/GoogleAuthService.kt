package com.example.uni_project.core


import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

class GoogleAuthService(private val context: Context) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("your_web_client_id_here") // Замените на реальный ID
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun signInWithIntent(data: Intent?): GoogleSignInResult {
        return try {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.await()
            GoogleSignInResult.Success(account)
        } catch (e: ApiException) {
            GoogleSignInResult.Error(e.statusCode, e.message ?: "Unknown error")
        } catch (e: Exception) {
            GoogleSignInResult.Error(-1, e.message ?: "Unknown error")
        }
    }

    suspend fun signOut() {
        googleSignInClient.signOut().await()
    }
}

sealed class GoogleSignInResult {
    data class Success(val account: GoogleSignInAccount) : GoogleSignInResult()
    data class Error(val code: Int, val message: String) : GoogleSignInResult()
}