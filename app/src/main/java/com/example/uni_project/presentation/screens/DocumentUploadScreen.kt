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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.uni_project.R
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.DateTransformation
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.core.SessionManager
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.image_choose.rememberImagePicker
import com.example.uni_project.presentation.viewmodel.DocumentUploadViewModel
import com.example.uni_project.presentation.viewmodel.factory.DocumentUploadViewModelFactory
import com.example.uni_project.ui.theme.Purple
import com.example.uni_project.ui.theme.Uni_projectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    email: String,
    onBack: () -> Unit,
    onRegistrationComplete: () -> Unit,
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

    val viewModel: DocumentUploadViewModel = viewModel(
        factory = DocumentUploadViewModelFactory(authRepository,sessionManager)
    )

    val state by viewModel.state.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()

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
                title = {
                    Text(
                        text = stringResource(R.string.registration_title),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.bodyMedium,
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
                        Image(
                            painter = rememberAsyncImagePainter(state.profilePhotoUri),
                            contentDescription = "Фото профиля",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = "Добавить фото",
                            modifier = Modifier
                                .size(60.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.photo),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 10.dp)

                    )

                Text(
                    text = stringResource(R.string.license),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 50.dp)
                )

                OutlinedTextField(
                    value = state.driverLicenseNumber,
                    onValueChange = { viewModel.updateDriverLicenseNumber(it) },
                    label = { Text(stringResource(R.string.license_mask)) },
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
                Text(
                    text = stringResource(R.string.date_),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = state.driverLicenseIssueDate,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() }
                        if (filtered.length <= 8) {
                            viewModel.updateDriverLicenseIssueDate(filtered)
                        }
                    },
                    label = { Text(stringResource(R.string.date)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    visualTransformation = DateTransformation(),
                    isError = state.issueDateError != null,
                    modifier = Modifier.fillMaxWidth()
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


                DocumentUploadSection(
                    title = "Загрузите фото водительского удостоверения",
                    isUploaded = state.driverLicensePhotoUri != null,
                    error = state.driverLicensePhotoError,
                    onUploadClick = {
                        driverLicensePhotoPicker.showImagePicker()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))


                DocumentUploadSection(
                    title = "Загрузите фото паспорта",
                    isUploaded = state.passportPhotoUri != null,
                    error = state.passportPhotoError,
                    onUploadClick = {
                        passportPhotoPicker.showImagePicker()
                    }
                )

                Spacer(modifier = Modifier.height(100.dp))


                Button(
                    onClick = {
                        focusManager.clearFocus()
                        scope.launch {
                            val success = viewModel.completeRegistration(email)

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

        TextButton(
            onClick = onUploadClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUploaded) {
                    Icon(Icons.Default.Check, contentDescription = "Загружено")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Фото загружено")
                } else {
                    Image(
                        painter = painterResource(R.drawable.upload),
                        contentDescription = "Загрузить",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Загрузить фото")
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DocumentUploadScreenPreview() {
//    Uni_projectTheme {
//        DocumentUploadScreen(
//            email = "test@example.com",
//            onBack = {},
//            onRegistrationComplete = {}
//        )
//    }
//}