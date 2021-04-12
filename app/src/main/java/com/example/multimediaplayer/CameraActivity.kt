package com.example.multimediaplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera.open
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
//import com.example.multimediaplayer.database.FilesDatabase
//import com.example.multimediaplayer.model.FileType
//import com.example.multimediaplayer.model.MediaFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null

    private lateinit var recorder: MediaRecorder

    private var fileName: String = ""

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraCaptureButton: Button
    private lateinit var viewFinder: PreviewView

    private var isRecording = false

//    private lateinit var database: FilesDatabase

//    private val cameraManager: CameraManager by lazy {
//        applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//    }
//    private lateinit var camera: CameraDevice

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

        // Set up the listener for take photo button
        cameraCaptureButton.setOnClickListener { takePhoto() }

        cameraCaptureButton.setOnLongClickListener { recordVideo() }

        outputDirectory = getOutputDirectory()

//        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory, "$fileName.jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)


//                    lifecycleScope.launch {
//                        val mediaFile = MediaFile(fileName, photoFile.absolutePath, "PHOTO", false, "", "", "")
//                        database.filesDao().insert(mediaFile)
//                    }
                }
            })

    }

    @SuppressLint("RestrictedApi")
    private fun recordVideo(): Boolean {
        val videoCapture = videoCapture ?: return false

        if (!isRecording) {
            Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show()

            val videoFile = File(
                outputDirectory,
                "$fileName.mp4"
            )

            videoCapture.startRecording(
                videoFile,
                cameraExecutor,
                object: VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        Log.i("AAAA", "Video File : $file")
                    }

                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        Log.i("AAAA", "Video Error: $message")
                    }
            })

            cameraCaptureButton.setBackgroundColor(Color.BLUE)

            isRecording = !isRecording
        } else {
            Toast.makeText(this, "Stop recording", Toast.LENGTH_SHORT).show()

            recorder.stop()

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
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder()
            .build()

//        val videoCaptureConfig = VideoCapture.DEFAULT_CONFIG.getConfig(null)
//
//        videoCapture = VideoCapture.Builder
//            .fromConfig(videoCaptureConfig)
//            .setAudioRecordSource(MediaRecorder.AudioSource.MIC)
//            .build()

//        recorder = MediaRecorder()
//        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT)
//        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
////        recorder.setVideoSize(, HEIGHT);
////        recorder.setVideoFrameRate(FRAME_RATE);
//        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
////        recorder.setVideoEncodingBitRate(VIDEO_BITRATE)
////        recorder.setAudioChannels(AUDIO_CHANNELS)
////        recorder.setAudioSamplingRate(SAMPLE_RATE)
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
////        recorder.setAudioEncodingBitRate(AUDIO_BITRATE)

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

//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }

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