package com.example.uni_project.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.presentation.viewmodel.RegistrationDetailsViewModel
import com.example.uni_project.core.data_class.Gender
import com.example.uni_project.R
import com.example.uni_project.core.AuthRepository
import com.example.uni_project.core.DateTransformation
import com.example.uni_project.ui.theme.Purple
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationDetailsScreen(
    email: String,
    authRepository: AuthRepository,
    detailsViewModel: RegistrationDetailsViewModel,
    onBack: () -> Unit,
    onRegistrationCont: () -> Unit,

    ) {

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current




    val state by detailsViewModel.state.collectAsState()
    val registrationResult by detailsViewModel.registrationResult.collectAsState()

    LaunchedEffect(Unit) {
        detailsViewModel.clearErrors()
    }


    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            is RegistrationResult.Success -> {
                onRegistrationCont()
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
                Spacer(modifier = Modifier.height(40.dp))



                Text(
                    text = stringResource(R.string.surname),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )


                // Поле Фамилия
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { detailsViewModel.updateLastName(it) },
                    label = { Text(stringResource(R.string.surname_place)) },
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

                Text(
                    text = stringResource(R.string.name),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )

                // Поле Имя
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { detailsViewModel.updateFirstName(it) },
                    label = { Text(stringResource(R.string.name_place)) },
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


                Text(
                    text = stringResource(R.string.patronymic),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )

                OutlinedTextField(
                    value = state.middleName,
                    onValueChange = { detailsViewModel.updateMiddleName(it) },
                    label = { Text(stringResource(R.string.patronymic_place)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.birth_date),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 32.dp)
                )

                OutlinedTextField(
                    value = state.birthDate,
                    onValueChange = { newValue ->

                        val filtered = newValue.filter { it.isDigit() }

                        if (filtered.length <= 8) {
                            detailsViewModel.updateBirthDate(filtered)
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
                    isError = state.birthDateError != null,
                    modifier = Modifier.fillMaxWidth()
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


                Text(
                    text = stringResource(R.string.gender),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Мужской
                    Row(
                        modifier = Modifier
                            .clickable { detailsViewModel.updateGender(Gender.MALE) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (state.gender == Gender.MALE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = if (state.gender == Gender.MALE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.male),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    }

                    // Женский
                    Row(
                        modifier = Modifier
                            .clickable { detailsViewModel.updateGender(Gender.FEMALE) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (state.gender == Gender.FEMALE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = if (state.gender == Gender.FEMALE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.female),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
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


                Spacer(modifier = Modifier.height(125.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        scope.launch {
                            detailsViewModel.completeRegistration(email)
                        }
                    },
                    enabled = detailsViewModel.isFormValid && !state.isLoading,
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
                            text = "Далее",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

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

        // Диалог ошибки
        if (registrationResult is RegistrationResult.Error) {
            val errorResult = registrationResult as RegistrationResult.Error
            AlertDialog(
                onDismissRequest = { detailsViewModel.clearRegistrationResult() },
                title = { Text("Ошибка регистрации") },
                text = { Text(errorResult.message) },
                confirmButton = {
                    Button(onClick = { detailsViewModel.clearRegistrationResult() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
