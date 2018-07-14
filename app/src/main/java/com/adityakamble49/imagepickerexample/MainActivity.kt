package com.adityakamble49.imagepickerexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    private val RC_PERMISSIONS = 101
    private val RC_SELECT_IMGAE = 103

    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindView()
        askRequiredPermissions()
    }

    private fun bindView() {
        btn_select_image.setOnClickListener {
            selectImage()
        }
    }

    private fun arePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun askRequiredPermissions() {
        if (!arePermissionGranted()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, RC_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            RC_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun selectImage() {
        val selectImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI)
        startActivityForResult(selectImageIntent, RC_SELECT_IMGAE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SELECT_IMGAE -> {
                val selectedImage = data?.data
                selectedImage?.let {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(it,
                            filePathColumn,
                            null,
                            null,
                            null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val imgPath = cursor.getString(columnIndex)
                    cursor.close()
                    displaySelectedImage(imgPath)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun displaySelectedImage(imagePath: String) {
        imageBitmap = BitmapFactory.decodeFile(imagePath)
        iv_selected_image.setImageBitmap(imageBitmap)
    }
}
