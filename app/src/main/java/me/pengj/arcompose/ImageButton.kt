package me.pengj.arcompose

import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.google.accompanist.glide.rememberGlidePainter


private const val TAG = "ImageButton"
@Composable
fun ImageButton(uri: Uri? = null,
                imageClicked: (Uri) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        uri?.let { imageUri ->
            Image(
                painter = rememberGlidePainter(
                    imageUri,
                    fadeIn = true
                ),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
                    .padding(32.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        imageClicked.invoke(imageUri)
                    }
            )

            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(imageUri.toFile().extension)
            MediaScannerConnection.scanFile(
                LocalContext.current,
                arrayOf(imageUri.toFile().absolutePath),
                arrayOf(mimeType)
            ) { _, uri ->
                Log.d(TAG,"Image capture scanned into media store: $uri")
            }
        }
    }
}