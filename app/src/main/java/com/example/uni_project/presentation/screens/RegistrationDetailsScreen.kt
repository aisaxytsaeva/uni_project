package com.example.uni_project.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.presentation.viewmodel.RegistrationDetailsViewModel
import com.example.uni_project.presentation.viewmodel.factory.RegistrationDetailsViewModelFactory
import com.example.uni_project.core.data_class.Gender
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationDetailsScreen(
    email: String,
    onBack: () -> Unit,
    onRegistrationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val authRepository = remember {
        AuthRepositoryImpl(
            AppDatabase.getInstance(context),
            GoogleAuthService(context),

        )
    }

    val viewModel: RegistrationDetailsViewModel = viewModel(
        factory = RegistrationDetailsViewModelFactory(context.applicationContext as AuthRepository)
    )

    val state by viewModel.state.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearErrors()
    }


    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            is RegistrationResult.Success -> {
                onRegistrationComplete()
            }
            is RegistrationResult.Error -> {
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дополнительные данные") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Индикатор прогресса
                Text(
                    text = "Шаг 2 из 2",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Личные данные",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Заполните информацию о себе",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )


                // Поле Фамилия
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = { Text("Фамилия *") },
                    placeholder = { Text("Иванов") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.lastNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                state.lastNameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Поле Имя
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = { Text("Имя *") },
                    placeholder = { Text("Иван") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.firstNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                state.firstNameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Поле Отчество (необязательное)
                OutlinedTextField(
                    value = state.middleName,
                    onValueChange = { viewModel.updateMiddleName(it) },
                    label = { Text("Отчество") },
                    placeholder = { Text("Иванович") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Поле Дата рождения
                OutlinedTextField(
                    value = state.birthDate,
                    onValueChange = { viewModel.updateBirthDate(it) },
                    label = { Text("Дата рождения *") },
                    placeholder = { Text("MM/DD/YYYY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    isError = state.birthDateError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Формат: MM/DD/YYYY",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp)
                )

                state.birthDateError?.let { error ->
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

                // Выбор пола
                Text(
                    text = "Пол *",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Кнопка Мужской
                    OutlinedButton(
                        onClick = { viewModel.updateGender(Gender.MALE) },
                        modifier = Modifier.weight(1f),
                        colors = if (state.gender == Gender.MALE) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        Text("Мужской")
                    }

                    // Кнопка Женский
                    OutlinedButton(
                        onClick = { viewModel.updateGender(Gender.FEMALE) },
                        modifier = Modifier.weight(1f),
                        colors = if (state.gender == Gender.FEMALE) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        Text("Женский")
                    }
                }

                state.genderError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp)
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка Завершить регистрацию
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        scope.launch {
                            val success = viewModel.completeRegistration(email)
                            // Если успешно, переход произойдет через LaunchedEffect
                        }
                    },
                    enabled = viewModel.isFormValid && !state.isLoading,
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
                            text = "Далее",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Информация о загрузке
                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сохранение данных в БД...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Диалог успешной регистрации
    if (registrationResult is RegistrationResult.Success) {
        val successResult = registrationResult as RegistrationResult.Success
        AlertDialog(
            onDismissRequest = {
                viewModel.clearRegistrationResult()
                onRegistrationComplete()
            },
            title = { Text("Регистрация завершена!") },
            text = {
                Column {
                    Text(successResult.message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Добро пожаловать в приложение!")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearRegistrationResult()
                        onRegistrationComplete()
                    }
                ) {
                    Text("На главный экран")
                }
            }
        )
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
