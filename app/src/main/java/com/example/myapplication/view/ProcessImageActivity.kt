package com.example.myapplication.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.utils.toast
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class ProcessImageActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 23
    private val storageActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                //Android is 11 (R) or above
                if(Environment.isExternalStorageManager()){
                    //Manage External Storage Permissions Granted
                    toast("onActivityResult: Manage External Storage Permissions Granted")
                }else{
                    toast("Storage Permissions Denied");
                }
            }else{
                toast("Version not supported");
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_image)

        if(!checkPermission()) {
            requestForStoragePermissions()
        }
        val fileName = intent.getStringExtra("imagePath")

        val imageFile = File(fileName)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            )
            .build()
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS) // Set timeout koneksi 10 detik
            .readTimeout(20, TimeUnit.SECONDS) // Set timeout baca 10 detik
            .writeTimeout(20, TimeUnit.SECONDS) // Set timeout tulis 10 detik
            .build()

        val request = Request.Builder()
            .url("https://image.smartlock.icu")
            .post(requestBody)
            .build()

        Toast.makeText(this, "Mengirim permintaan ke server", Toast.LENGTH_SHORT).show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                imageFile.delete()
                Log.e("UploadError", "Gagal mengunggah file", e)
                runOnUiThread {
                    val intent = Intent(this@ProcessImageActivity, MainActivity::class.java)
                    intent.putExtra("message", "failed connect to server")
                    startActivity(intent)
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    imageFile.delete()

                    val responseBody = response.body?.string()

                    if (!responseBody.isNullOrEmpty() && response.isSuccessful) {
                        val gson = Gson()
                        val responseObject = gson.fromJson(responseBody, ResponseServer::class.java)

                        // Lakukan sesuatu dengan objek respons, misalnya:
                        val status = responseObject.status
                        val message = responseObject.message
                        Log.d("ResponseStatus", "Status: $status, Message: $message")

                        if(status == "success") {
                            // Tangani respons sukses dari server
                            runOnUiThread {
                                startActivity(Intent(this@ProcessImageActivity, HomeActivity::class.java))
                                finish()
                            }
                        } else if (status == "error") {
                            runOnUiThread {
                                val intent = Intent(this@ProcessImageActivity, MainActivity::class.java)
                                intent.putExtra("message", "wajah tidak di kenali")
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        })
    }

    fun checkPermission(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        }
        val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
    }

    fun requestForStoragePermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent);
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if(read && write){
                    toast("Storage Permissions Granted")
                }else{
                    toast("Storage Permissions Denied")
                }
            }
        }
    }
}

data class ResponseServer (
    val status: String,
    val message: String
)