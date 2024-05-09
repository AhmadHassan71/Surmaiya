package com.smd.surmaiya.ManagerClasses

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseStorageManager {

    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Reference to the images folder
    private val imagesRef: StorageReference
        get() = storageInstance.reference.child("images")

    fun uploadImage(imageUri: Uri, onSuccess: (imagePath: String) -> Unit) {
        // Delete the old image
        val oldImagePath = UserManager.getCurrentUser()?.profilePictureUrl
        if (oldImagePath != null && oldImagePath.isNotEmpty()) {
            val oldImageRef = storageInstance.getReferenceFromUrl(oldImagePath)
            oldImageRef.delete()
                .addOnSuccessListener {
                    Log.d("FirebaseStorageManager", "Old image deleted successfully")
                }
                .addOnFailureListener {
                    Log.e("FirebaseStorageManager", "Failed to delete old image: ${it.message}")
                }
        }

        // Upload the new image
        val ref = imagesRef.child(UUID.randomUUID().toString())
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("FirebaseStorageManager", "Image uploaded successfully: ${it.toString()}")
                    onSuccess(it.toString())
                }
            }
            .addOnFailureListener {
                Log.e("FirebaseStorageManager", "Image upload failed: ${it.message}")
            }
    }
}