package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.User
import java.security.MessageDigest
import java.util.UUID


object FirebaseDatabaseManager {

    // Singleton instance
    private var instance: FirebaseDatabaseManager? = null
    private val database = FirebaseDatabase.getInstance()

    @JvmStatic
    fun getInstance(): FirebaseDatabaseManager {
        if (instance == null) {
            instance = FirebaseDatabaseManager
        }
        return instance!!
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }


    fun signUpUser(user: User, activity:AppCompatActivity, Callback: (Boolean) -> Unit){

        val myRef = database.getReference("users")
        val userId = myRef.push().key
        user.id = userId.toString()
        user.password = hashPassword(user.password)
        UserManager.setCurrentUser(user)

        myRef.child(userId.toString()).setValue(user)
            .addOnSuccessListener {
                Log.d("Signing Up User In Database", "User data saved successfully")
                FirebaseAuthManager(activity).saveUserAuthentication(user)
                {
                    Callback(it)

                }

            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error saving user data: ${e.message}")
                Callback(false)

            }

    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        val myRef = database.getReference("users")

        myRef.child(user.id).setValue(user)
            .addOnSuccessListener {
                Log.d("Updating User In Database", "User data updated successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error updating user data: ${e.message}")
                callback(false)
            }
    }

    fun uploadAlbumAndSongs(album: Album, songs: List<Song>) {
        val database = FirebaseDatabase.getInstance()
        // Upload the album
        val albumRef = database.getReference("Albums").child(album.id)
        albumRef.setValue(album)


        // Upload the songs
        for (song in songs) {
            val songRef = database.getReference("Songs").child(song.id)
            songRef.setValue(song)

            // Update the genres
            for (genre in song.genres) {
                val genreId = UUID.randomUUID().toString()
                val genreRef = database.getReference("Genres").child(genre).child("id")
                genreRef.setValue(genreId)
            }
        }
    }

}

