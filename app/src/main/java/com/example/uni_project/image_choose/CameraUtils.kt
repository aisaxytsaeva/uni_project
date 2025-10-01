package com.example.uni_project.image_choose

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import androidx.camera.core.ImageCaptureException
import android.util.Log
import android.os.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine


internal fun initializeCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onCameraInitialized: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
            onCameraInitialized(imageCapture)
        } catch (exc: Exception) {
            Log.e("CameraCapture", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

internal const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
internal const val PHOTO_EXTENSION = ".jpg"

internal fun Context.getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, "Camera").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}

internal suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

internal fun capturePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onImageCaptured: (Uri) -> Unit
) {
    val outputDirectory = context.getOutputDirectory()
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis()) + PHOTO_EXTENSION
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)

                // Добавляем фото в галерею
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        photoFile.absolutePath,
                        photoFile.name,
                        null
                    )
                }

                onImageCaptured(savedUri)
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraCapture", "Photo capture failed: ${exc.message}", exc)
            }
        }
    )
}