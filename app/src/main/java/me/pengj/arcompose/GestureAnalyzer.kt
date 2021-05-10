package me.pengj.arcompose

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzerFactory

private const val TAG = "GestureAnalyzer"
class GestureAnalyzer(private val onGestureDetected: (Int) -> Unit) : ImageAnalysis.Analyzer {

    private val analyzer = MLGestureAnalyzerFactory
        .getInstance().gestureAnalyzer

    private var inprogress = false

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (inprogress) {
            return
        }

        imageProxy.image?.let {
            inprogress = true

            analyzer.asyncAnalyseFrame(MLFrame.fromMediaImage(it, 1))
                .addOnSuccessListener { list ->
                    if (list.isNotEmpty()) {
                        val category = list.first().category
                        onGestureDetected.invoke(category)
                        Log.e(TAG, "onGestureDetected: $category")
                    }
                    inprogress = false
                    imageProxy.close()
                }.addOnFailureListener {
                    Log.e(TAG, it.toString())
                    inprogress = false
                    imageProxy.close()
                }
        }
    }
}