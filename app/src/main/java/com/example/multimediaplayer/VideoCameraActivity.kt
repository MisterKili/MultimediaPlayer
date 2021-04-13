package com.example.multimediaplayer

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.core.impl.CaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File

class VideoCameraActivity : CameraActivityBase() {

    private var videoCapture: VideoCapture? = null

    private var isRecording = false

//    private lateinit var database: FilesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraCaptureButton.setText(R.string.start_video)

        cameraCaptureButton.setOnClickListener { recordVideo() }
    }

    @SuppressLint("RestrictedApi")
    private fun recordVideo(): Boolean {
        val videoCapture = videoCapture ?: return false

        if (!isRecording) {
            cameraCaptureButton.setBackgroundColor(Color.GREEN)

            val videoFile = File(
                outputDirectory,
                "$fileName.avi"
            )

            videoCapture.startRecording(
                videoFile,
                ContextCompat.getMainExecutor(this),
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.video_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(
                        videoCaptureError: Int,
                        message: String,
                        cause: Throwable?
                    ) {
                        Log.i("AAAA", "Video Error: $message")
                    }
                })

            isRecording = !isRecording
        } else {
            cameraCaptureButton.setBackgroundColor(Color.RED)
            cameraCaptureButton.setText(R.string.stop_video)

            videoCapture.stopRecording()

            isRecording = !isRecording
        }

        return true
    }

    @SuppressLint("RestrictedApi")
    override fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        val videoCaptureConfig = VideoCapture.DEFAULT_CONFIG.getConfig(null)

        videoCapture = VideoCapture.Builder
            .fromConfig(videoCaptureConfig)
            .setDefaultCaptureConfig(CaptureConfig.defaultEmptyCaptureConfig())
            .build()
    }
}