package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Playlist
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

    fun getPlaylists(callback: (List<Playlist>) -> Unit) {
        val playlistRef = database.getReference("Playlist")
        playlistRef.get().addOnSuccessListener { snapshot ->
            val playlists = mutableListOf<Playlist>()
            val objectsMap = snapshot.value as Map<*, *>
            for ((_, value) in objectsMap) {
                val playlistMap = value as Map<*, *>
                val playlist = Playlist(
                    playlistMap["playlsitId"] as String,
                    playlistMap["playlistName"] as String,
                    playlistMap["songids"] as Map<String, Any>,
                    playlistMap["coverArtUrl"] as String,
                    playlistMap["userIds"] as Map<String, Any>,
                    (playlistMap["dateAdded"] as Map<String, Any>)["date"] as String,
                    playlistMap["followers"] as Long,
                    playlistMap["visibility"] as String
                )
                playlists.add(playlist)
            }
            callback(playlists)
        }
    }

    fun fetchSongFromFirebase(songId: String, callback: (Song) -> Unit) {
        val songRef = FirebaseDatabase.getInstance().getReference("Songs").child(songId)
        songRef.get().addOnSuccessListener { snapshot ->
            val song = snapshot.getValue(Song::class.java)
            if (song != null) {
                Log.d("FirebaseSong", "Fetched song with ID: $songId")
                callback(song)
            } else {
                Log.d("FirebaseSong", "No song found with ID: $songId")
            }
        }.addOnFailureListener { exception ->
            Log.d("FirebaseSong", "Failed to fetch song with ID: $songId", exception)
        }
    }

}

