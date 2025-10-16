package com.example.uni_project.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uni_project.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.core.SessionManager
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.presentation.viewmodel.RegistrationViewModel
import com.example.uni_project.presentation.viewmodel.factory.RegistrationViewModelFactory
import com.example.uni_project.ui.theme.Purple

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onRegister: (String, String) -> Unit,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val authRepository = remember {
        AuthRepositoryImpl(
            AppDatabase.getInstance(context),
            GoogleAuthService(context),
            sessionManager = sessionManager

        )
    }


    val viewModel: RegistrationViewModel = viewModel(
        factory = RegistrationViewModelFactory(authRepository, sessionManager)
    )


    val state by viewModel.registrationState.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearErrors()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.registration_title),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable { focusManager.clearFocus() },
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {



                Text(
                    text = stringResource(R.string.mail),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 150.dp)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text(stringResource(R.string.mail_sign)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.emailError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                state.emailError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }


                Text(
                    text = stringResource(R.string.create_password),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text(stringResource(R.string.password_sign)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = if (state.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = state.passwordError != null,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (state.isPasswordVisible) {
                                    "Скрыть пароль"
                                } else {
                                    "Показать пароль"
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                state.passwordError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.confirm_password_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    label = { Text(stringResource(R.string.password_sign)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    visualTransformation = if (state.isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = state.confirmPasswordError != null,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (state.isConfirmPasswordVisible) {
                                    "Скрыть пароль"
                                } else {
                                    "Показать пароль"
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                state.confirmPasswordError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleTermsAccepted() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.isTermsAccepted,
                        onCheckedChange = { viewModel.toggleTermsAccepted() }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.terms_agreement),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                state.termsError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(170.dp))


                Button(
                    onClick = {
                        focusManager.clearFocus()
                        scope.launch {
                            viewModel.registerStep1()
                        }
                    },
                    enabled = viewModel.isFormValid && !state.isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.cont),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }


                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сохранение данных...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Диалог ошибки
    if (registrationResult is RegistrationResult.Error) {
        val errorResult = registrationResult as RegistrationResult.Error
        AlertDialog(
            onDismissRequest = { viewModel.clearRegistrationResult() },
            title = { Text("Ошибка регистрации") },
            text = { Text(errorResult.message) },
            confirmButton = {
                Button(onClick = { viewModel.clearRegistrationResult() }) {
                    Text("OK")
                }
            }
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun Reg(){
//    RegistrationScreen(
//        onBack = {},
//        onRegister = { email, password -> }
//    )
//}