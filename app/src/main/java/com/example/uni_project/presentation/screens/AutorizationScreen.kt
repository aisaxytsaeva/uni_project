package com.example.uni_project.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uni_project.R
import com.example.uni_project.core.data_class.GoogleSignInEvent
import com.example.uni_project.presentation.viewmodel.AuthViewModel
import com.example.uni_project.ui.theme.Purple

@Composable
fun AuthorizationScreen(
    onAuthorization: () -> Unit,
    onRegistration: () -> Unit,
    authViewModel: AuthViewModel

) {

    val scope = rememberCoroutineScope()
    val state by authViewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    val authResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            authViewModel.handleGoogleSignInResult(result)
        }
    )


    LaunchedEffect(Unit) {
        authViewModel.googleSignInEvent.collect { event ->
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
        authViewModel.clearError()
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
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(top = 100.dp)

            )

            Text(
                text = stringResource(R.string.sign_in_des),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {


            Text(
                text = stringResource(R.string.mail),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 150.dp)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { authViewModel.updateEmail(it) },
                label = { Text(stringResource(R.string.mail_sign)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp)
            )

            Text(
                text = stringResource(R.string.password),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 32.dp)
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { authViewModel.updatePassword(it) },
                label = { Text(stringResource(R.string.password_sign)) },
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { authViewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Видимость пароля"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp)
            )

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            TextButton(
                onClick = onRegistration,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Purple
                )
            ) {
                Text(
                    text = stringResource(R.string.forgotten_password),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    authViewModel.login { result ->
                        if (result.success) onAuthorization()
                    }
                },
                enabled = authViewModel.isFormValid && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.sign_in_))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.prepareGoogleSignIn()
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row {

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sign_in_g))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = onRegistration,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Purple
                )
            ) {
                Text(
                    text = stringResource(R.string.registration),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun Auto(){
//    AuthorizationScreen(
//        onAuthorization = {},
//        onRegistration = {}
//    )
//}