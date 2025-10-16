package com.example.uni_project.image_choose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun rememberImagePicker(
    onImageSelected: (Uri) -> Unit
): ImagePickerController {
    var showImagePicker by remember { mutableStateOf(false) }
    var useCamera by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
        showImagePicker = false
    }

    val imagePickerController = remember {
        ImagePickerController(
            showImagePicker = { showImagePicker = true },
            hideImagePicker = {
                showImagePicker = false
                useCamera = false
            }
        )
    }

    if (showImagePicker) {
        if (useCamera) {
            // Оборачиваем камеру в Dialog для полноэкранного режима
            Dialog(
                onDismissRequest = {
                    showImagePicker = false
                    useCamera = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    CameraCaptureScreen(
                        onImageCaptured = { uri ->
                            onImageSelected(uri)
                            showImagePicker = false
                            useCamera = false
                        },
                        onBack = {
                            showImagePicker = false
                            useCamera = false
                        }
                    )
                }
            }
        } else {
            ImagePickerOptions(
                onCameraSelected = { useCamera = true },
                onGallerySelected = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCancel = { showImagePicker = false }
            )
        }
    }

    return imagePickerController
}


class ImagePickerController(
    val showImagePicker: () -> Unit,
    val hideImagePicker: () -> Unit
)


@Composable
fun ImagePickerOptions(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onCancel: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Выберите источник изображения",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {

                Button(
                    onClick = onCameraSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = onGallerySelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text("Отмена")
            }
        }
    )
}