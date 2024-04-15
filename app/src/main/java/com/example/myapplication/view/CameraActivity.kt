package com.example.myapplication.view

import android.content.ContentValues
import android.content.Intent
import androidx.camera.lifecycle.ProcessCameraProvider
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityCameraBinding
import com.example.myapplication.utils.toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    private val permissionId = 15
    private val multiplePermissionNameList = arrayListOf(
        android.Manifest.permission.CAMERA,
    )


    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private var orientationEventListener: OrientationEventListener? = null
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var aspectRatio = AspectRatio.RATIO_16_9
    private lateinit var videoCapture: VideoCapture<Recorder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        setContentView(binding.root)

        if (checkMultiplePermission()) {
            startCamera()
        }

        binding.proccess.setOnClickListener {
            takePhoto()
        }
    }

    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                permissionId
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }
                if (isGrant) {
                    // here all permission granted successfully
                    startCamera()
                } else {
                    checkMultiplePermission()
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUserCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUserCases() {
        val rotation = binding.camera.display.rotation

        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(
                AspectRatioStrategy(
                    aspectRatio,
                    AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            )
            .build()

        val preview = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setSurfaceProvider(binding.camera.surfaceProvider)
            }

        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                )
            )
            .setAspectRatio(aspectRatio)
            .build()

        videoCapture = VideoCapture.withOutput(recorder).apply {
            targetRotation = rotation
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val myRotation = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture.targetRotation = myRotation
                videoCapture.targetRotation = myRotation
            }
        }
        orientationEventListener?.enable()

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture,videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhoto() {
        val imageFolder = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "Images"
        )
        if (!imageFolder.exists()) {
            imageFolder.mkdir()
        }

        val fileName = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Images")
            }
        }

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = (lensFacing == CameraSelector.LENS_FACING_FRONT)
        }
        val outputOption =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                ImageCapture.OutputFileOptions.Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).setMetadata(metadata).build()
            }else{
                val imageFile = File(imageFolder, fileName)
                ImageCapture.OutputFileOptions.Builder(imageFile)
                    .setMetadata(metadata).build()
            }
        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    if (savedUri != null) {
                        val projection = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = contentResolver.query(savedUri, projection, null, null, null)
                        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor?.moveToFirst()
                        val imagePath = cursor?.getString(columnIndex ?: -1)
                        cursor?.close()

                        if (!imagePath.isNullOrEmpty()) {
                            // Memproses gambar yang berhasil disimpan
                            val intent = Intent(this@CameraActivity, ProcessImageActivity::class.java)
                            intent.putExtra("imagePath", imagePath)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@CameraActivity,
                                "Failed to process photo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@CameraActivity,
                            "Failed to process photo.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        exception.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    private fun processImage(uri: Uri) {
        binding.loading.visibility = View.VISIBLE
        val file = File(uri.path)
        Log.i("proccessImage", "processImage: ${uri.path}")

        if (file.exists()) {
           Toast.makeText(this, "File dapat di akses", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "File tidak ada atau tidak dapat diakses: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        }

        val scheme = uri.scheme
        Log.i("scema image", "processImage: $scheme")
        if (scheme == "content") {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), stream.readBytes())

                val request = Request.Builder()
                    .url("https://image.smartlock.icu")
                    .post(requestBody)
                    .build()

                Toast.makeText(this, "Mengirim permintaan ke server", Toast.LENGTH_SHORT).show()

                OkHttpClient().newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        binding.loading.visibility = View.INVISIBLE
                        Log.e("UploadError", "Gagal mengunggah file", e)
                        // Tangani kegagalan mengunggah file
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            binding.loading.visibility = View.INVISIBLE

                            if (response.isSuccessful) {
                                Log.d("UploadSuccess", "Berhasil mengunggah file")
                                // Tangani respons sukses dari server
                                startActivity(Intent(this@CameraActivity, HomeActivity::class.java))
                                finish()
                            }
                        }
                    }
                })
            } ?: run {
                Log.e("FileError", "Gagal membuka inputStream dari URI: $uri")
                binding.loading.visibility = View.INVISIBLE
                // Tangani error jika gagal membuka inputStream
            }
        } else if (scheme == "file") {
            val file = File(uri.path)
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    RequestBody.create("image/*".toMediaTypeOrNull(), file)
                )
                .build()

            val request = Request.Builder()
                .url("https://image.smartlock.icu")
                .post(requestBody)
                .build()

            Toast.makeText(this, "Mengirim permintaan ke server", Toast.LENGTH_SHORT).show()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    binding.loading.visibility = View.INVISIBLE
                    file.delete()
                    Log.e("UploadError", "Gagal mengunggah file", e)
                    // Tangani kegagalan mengunggah file
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        binding.loading.visibility = View.INVISIBLE
                        file.delete()
                        if (response.isSuccessful) {
                            Log.d("UploadSuccess", "Berhasil mengunggah file")
                            // Tangani respons sukses dari server
                            startActivity(Intent(this@CameraActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }
            })
        } else {
            Log.e("FileError", "Skema URI tidak didukung: $scheme")
            binding.loading.visibility = View.INVISIBLE
            // Tangani skema URI yang tidak didukung
        }
    }
}