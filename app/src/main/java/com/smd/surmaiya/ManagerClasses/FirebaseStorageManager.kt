package com.smd.surmaiya.ManagerClasses

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseStorageManager {

    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Reference to the images folder
    private val imagesRef: StorageReference
        get() = storageInstance.reference.child("images")

    fun uploadImage(imageUri: Uri, onUploadStart: () -> Unit, onSuccess: (imagePath: String) -> Unit) {
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
        onUploadStart() // Call the onUploadStart callback
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
    fun uploadToFirebaseStorage(filePath: Uri, path: String): String {
        Log.d("AddSongFragment", "Uploading to Firebase Storage")

        // Create a storage reference
        val storageRef = FirebaseStorage.getInstance().reference.child(path)

        var downloadUrl =""

        // Upload the file
        val uploadTask = storageRef.putFile(filePath).addOnSuccessListener {
            /// Handle successful uploads
            Log.d("AddSongFragment", "Upload succeeded")
            //get download URL
            storageRef.downloadUrl.addOnSuccessListener {
                downloadUrl=it.toString()
            }.addOnFailureListener {
                Log.d("AddSongFragment", "Failed to get download URL")
            }
        }

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d("AddSongFragment", "Upload failed")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Log.d("AddSongFragment", "Upload succeeded")
        }

        return downloadUrl

    }


}