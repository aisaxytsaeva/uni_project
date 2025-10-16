package com.example.uni_project.image_choose

import android.Manifest
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (Uri) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            initializeCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onCameraInitialized = { capture -> imageCapture = capture }
            )
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            imageCapture = imageCapture
        )

        // Кнопка назад
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Назад",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }

        // Кнопка съемки
        IconButton(
            onClick = {
                imageCapture?.let { capture ->
                    capturePhoto(
                        imageCapture = capture,
                        context = context,
                        onImageCaptured = onImageCaptured
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Сделать фото",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }
    }
}