package me.pengj.arcompose


import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.gesture.MLGesture
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class GestureViewModel : ViewModel() {
    private var capturing = false

    var captureFileUri by mutableStateOf<Uri?>(null)
        private set

    private lateinit var imageCapture: ImageCapture
    private lateinit var outputFolder: File

    fun setGestureCode(gesture: Int) {
        if (gesture == MLGesture.GOOD) {
            takePicture()
        }
    }
    fun viewImage(uri: Uri) {
        Log.e(TAG, "view Image: $uri")
    }

    fun setupImageCapture(rotation: Int, outputFolder: File): ImageCapture {
        if (!this::imageCapture.isInitialized) {
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(rotation)
                .build()
            this.outputFolder = outputFolder
        }
        Log.e(TAG, "return imageCapture: $imageCapture")
        return imageCapture
    }

    private fun takePicture() {
        runBlocking {
            if (!capturing) {
                capturing = true
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000L)
                    capturing = false
                    captureFileUri = null
                }
                capture()
            }
        }
    }

    private fun capture() {
        Log.e("GestureViewModel", "takePicture")
        // Create output file to hold the image
        val photoFile = createFile(outputFolder, FILENAME, PHOTO_EXTENSION)

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .build()

        imageCapture.takePicture(outputOptions, Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    captureFileUri = output.savedUri ?: Uri.fromFile(photoFile)
                    Log.e(TAG, "Photo capture succeeded: $captureFileUri")
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture exception: $exception")
                }
            })
    }



    companion object {

        private const val TAG = "GestureViewModel"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}