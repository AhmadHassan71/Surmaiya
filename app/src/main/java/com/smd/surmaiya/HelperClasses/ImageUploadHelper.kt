package com.smd.surmaiya.HelperClasses

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import java.io.ByteArrayOutputStream

object ImageUploadUtils {

    private const val GALLERY = 1
    private const val CAMERA = 2

    fun showImageUploadDialog(fragment: Fragment) {
        val dialog = Dialog(fragment.requireContext())
        dialog.setContentView(R.layout.dialog_select_image)

        val btnGallery = dialog.findViewById<TextView>(R.id.galleryText)
        val btnCamera = dialog.findViewById<TextView>(R.id.photoCameraText)

        btnGallery.setOnClickListener {
            choosePhotoFromGallery(fragment)
            dialog.dismiss()
        }

        btnCamera.setOnClickListener {
            takePhotoFromCamera(fragment)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun choosePhotoFromGallery(fragment: Fragment) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        fragment.startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera(fragment: Fragment) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(intent, CAMERA)
    }

    fun handleImageUploadResult(requestCode: Int, resultCode: Int, data: Intent?, fragment: Fragment, view: View, onImageUploadSuccess: (imagePath: String) -> Unit) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                GALLERY, CAMERA -> {
                    var contentURI = if (requestCode == GALLERY) data.data else Uri.parse(MediaStore.Images.Media.insertImage(fragment.requireContext().contentResolver, data.extras?.get("data") as Bitmap, "Title", null))

                    // Compress the image
                    val bitmap = MediaStore.Images.Media.getBitmap(fragment.requireContext().contentResolver, contentURI)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    val byteArray = outputStream.toByteArray()
                    contentURI = Uri.parse(MediaStore.Images.Media.insertImage(fragment.requireContext().contentResolver, Bitmap.createScaledBitmap(bitmap, bitmap.width/2, bitmap.height/2, false), "Title", null))

                    val snackbar = Snackbar.make(view, "Uploading photo...", Snackbar.LENGTH_INDEFINITE)
                    snackbar.show()
                    FirebaseStorageManager.uploadImage(contentURI,
                        onUploadStart = {},
                        onSuccess = { imagePath ->
                            snackbar.dismiss() // Dismiss the Snackbar
                            onImageUploadSuccess(imagePath)
                        }
                    )
                }
            }
        }
    }
}