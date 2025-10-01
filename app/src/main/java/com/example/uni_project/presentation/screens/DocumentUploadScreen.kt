package com.example.uni_project.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.image_choose.rememberImagePicker
import com.example.uni_project.presentation.viewmodel.DocumentUploadViewModel
import com.example.uni_project.presentation.viewmodel.factory.DocumentUploadViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
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

    val viewModel: DocumentUploadViewModel = viewModel(
        factory = DocumentUploadViewModelFactory(authRepository)
    )

    val state by viewModel.state.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()

    // Контроллеры для выбора изображений
    val profilePhotoPicker = rememberImagePicker { uri ->
        viewModel.updateProfilePhoto(uri.toString())
    }

    val driverLicensePhotoPicker = rememberImagePicker { uri ->
        viewModel.updateDriverLicensePhoto(uri.toString())
    }

    val passportPhotoPicker = rememberImagePicker { uri ->
        viewModel.updatePassportPhoto(uri.toString())
    }

    LaunchedEffect(Unit) {
        viewModel.clearErrors()
    }

    // Обработка завершения регистрации
    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            is RegistrationResult.Success -> {
                onRegistrationComplete()
            }
            is RegistrationResult.Error -> {
                // Ошибка обрабатывается в UI
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Загрузка документов") },
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

                Text(
                    text = "Документы",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Загрузите необходимые документы",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // Фото профиля (необязательное)
                Text(
                    text = "Фото профиля (необязательно)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            profilePhotoPicker.showImagePicker()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.profilePhotoUri != null) {
                        // Показать выбранное фото
                        Image(
                            painter = rememberImagePainter(state.profilePhotoUri),
                            contentDescription = "Фото профиля",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Добавить фото",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Добавить фото", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Номер водительского удостоверения
                OutlinedTextField(
                    value = state.driverLicenseNumber,
                    onValueChange = { viewModel.updateDriverLicenseNumber(it) },
                    label = { Text("Номер водительского удостоверения *") },
                    placeholder = { Text("AB123456") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.driverLicenseError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                state.driverLicenseError?.let { error ->
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

                // Дата выдачи
                OutlinedTextField(
                    value = state.driverLicenseIssueDate,
                    onValueChange = { viewModel.updateDriverLicenseIssueDate(it) },
                    label = { Text("Дата выдачи *") },
                    placeholder = { Text("DD/MM/YYYY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    isError = state.issueDateError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Формат: DD/MM/YYYY",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp)
                )

                state.issueDateError?.let { error ->
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

                // Загрузка фото водительского удостоверения
                DocumentUploadSection(
                    title = "Фото водительского удостоверения *",
                    isUploaded = state.driverLicensePhotoUri != null,
                    error = state.driverLicensePhotoError,
                    onUploadClick = {
                        driverLicensePhotoPicker.showImagePicker()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Загрузка фото паспорта
                DocumentUploadSection(
                    title = "Фото паспорта *",
                    isUploaded = state.passportPhotoUri != null,
                    error = state.passportPhotoError,
                    onUploadClick = {
                        passportPhotoPicker.showImagePicker()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка Завершить регистрацию
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        scope.launch {
                            val success = viewModel.completeDocumentUpload(email)
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
                            text = "Завершить регистрацию",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Информация о загрузке
                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сохранение документов в БД...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DocumentUploadSection(
    title: String,
    isUploaded: Boolean,
    error: String?,
    onUploadClick: () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onUploadClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isUploaded) {
                Icon(Icons.Default.Check, contentDescription = "Загружено")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Фото загружено")
            } else {
                Icon(Icons.Default.Upload, contentDescription = "Загрузить")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Загрузить фото")
            }
        }

        error?.let {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}