package com.example.multimediaplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.core.impl.CaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class VideoCameraActivity : AppCompatActivity() {

    private var videoCapture: VideoCapture? = null

    private var fileName: String = ""

    private lateinit var outputDirectory: File
    private lateinit var cameraCaptureButton: Button
    private lateinit var viewFinder: PreviewView

    private var isRecording = false

//    private lateinit var database: FilesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

//        database = FilesDatabase.getDatabase(this)

        setFileName()

        cameraCaptureButton = findViewById(R.id.camera_capture_button)
        viewFinder = findViewById(R.id.viewFinder)

        cameraCaptureButton.setOnClickListener { recordVideo() }

        outputDirectory = getOutputDirectory()
    }

    @SuppressLint("RestrictedApi")
    private fun recordVideo(): Boolean {
        val videoCapture = videoCapture ?: return false

        if (!isRecording) {
            Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show()

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
                        Log.i("AAAA", "Video File : $file")
                        Toast.makeText(applicationContext, "Recorded", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Stop recording", Toast.LENGTH_SHORT).show()

            videoCapture.stopRecording()

            cameraCaptureButton.setBackgroundColor(Color.RED)

            isRecording = !isRecording
        }

        return true
    }

    private fun setFileName() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Title")

        // Set up the input
        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> fileName = input.text.toString() }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera() {
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}