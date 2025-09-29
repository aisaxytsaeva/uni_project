package com.example.uni_project.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uni_project.presentation.viewmodel.AuthViewModel
import com.example.uni_project.presentation.viewmodel.factory.AuthViewModelFactory
import com.example.uni_project.R
import com.example.uni_project.core.data_class.GoogleSignInEvent

@Composable
fun AuthorizationScreen(
    onAuthorization: () -> Unit,
    onRegistration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val state by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Обработка результата Google Sign-In
    val authResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            viewModel.handleGoogleSignInResult(result)
        }
    )

    // Обработка событий Google Sign-In
    LaunchedEffect(Unit) {
        viewModel.googleSignInEvent.collect { event ->
            when (event) {
                is GoogleSignInEvent.LaunchSignIn -> {
                    authResultLauncher.launch(event.signInIntent)
                }
                is GoogleSignInEvent.Success -> {
                    onAuthorization()
                }
                is GoogleSignInEvent.Error -> {

                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { focusManager.clearFocus() },
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.sign_in_des),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Поля ввода
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Видимость пароля"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            // Сообщение об ошибке
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.login { result ->
                        if (result.success) onAuthorization()
                    }
                },
                enabled = viewModel.isFormValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.sign_in_))
                }
            }

            // Кнопка входа через Google
            OutlinedButton(
                onClick = {
                    viewModel.prepareGoogleSignIn()
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row {

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sign_in_g))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка регистрации
            TextButton(onClick = onRegistration) {
                Text(stringResource(R.string.registration))
            }
        }
    }
}