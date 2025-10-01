package com.example.uni_project.image_choose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun rememberImagePicker(
    onImageSelected: (Uri) -> Unit
): ImagePickerController {
    var showImagePicker by remember { mutableStateOf(false) }

    val imagePickerController = remember {
        ImagePickerController(
            showImagePicker = { showImagePicker = true },
            hideImagePicker = { showImagePicker = false }
        )
    }

    if (showImagePicker) {
        ImagePickerScreen(
            onImageSelected = { uri ->
                onImageSelected(uri)
                showImagePicker = false
            },
            onCancel = {
                showImagePicker = false
            }
        )
    }

    return imagePickerController
}

// Контроллер для управления пикером изображений
class ImagePickerController(
    val showImagePicker: () -> Unit,
    val hideImagePicker: () -> Unit
)

// Экран выбора между камерой и галереей
@Composable
fun ImagePickerScreen(
    onImageSelected: (Uri) -> Unit,
    onCancel: () -> Unit = {}
) {
    var showPickerOptions by remember { mutableStateOf(true) }
    var useCamera by remember { mutableStateOf(false) }

    if (showPickerOptions) {
        ImagePickerOptions(
            onCameraSelected = {
                useCamera = true
                showPickerOptions = false
            },
            onGallerySelected = {
                useCamera = false
                showPickerOptions = false
            },
            onCancel = onCancel
        )
    } else {
        if (useCamera) {
            CameraCaptureScreen(
                onImageCaptured = { uri ->
                    onImageSelected(uri)
                    showPickerOptions = true
                },
                onBack = {
                    showPickerOptions = true
                }
            )
        } else {
            GalleryPickerScreen(
                onImageSelected = { uri ->
                    onImageSelected(uri)
                    showPickerOptions = true
                },
                onBack = {
                    showPickerOptions = true
                }
            )
        }
    }
}

@Composable
fun ImagePickerOptions(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onCancel: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Выберите источник изображения",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Кнопка камеры
        Button(
            onClick = onCameraSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Камера",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Сделать фото")
        }

        // Кнопка галереи
        Button(
            onClick = onGallerySelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Галерея",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Выбрать из галереи")
        }

        // Кнопка отмены
        TextButton(
            onClick = onCancel,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Отмена")
        }
    }
}