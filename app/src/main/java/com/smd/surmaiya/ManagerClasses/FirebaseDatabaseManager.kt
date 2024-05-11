package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
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
            Log.d("FirebasePlaylist", "Fetched all playlists ${snapshot.value}")
            val playlists = mutableListOf<Playlist>()
            val objectsMap = snapshot.value as Map<*, *>
            for ((_, value) in objectsMap) {
                val playlistMap = value as Map<*, *>
                val playlist = Playlist(
                    playlistMap["playlsitId"] as String,
                    playlistMap["playlistName"] as String,
                    playlistMap["songIds"] as List<String>,
                    playlistMap["coverArtUrl"] as String,
                    playlistMap["userIds"] as List<String>,
                    playlistMap["dateAdded"] as List<String>,
                    playlistMap["followers"] as Long,
                    playlistMap["followerIds"] as List<String>,
                    playlistMap["visibility"] as String,
                    playlistMap["playlistDescription"]as String,
                )
                playlists.add(playlist)
                Log.d("FirebasePlaylist", "Fetched playlist with ID: ${playlist.playlsitId}")
                Log.d("FirebasePlaylist", "Fetched playlist with name: ${playlist.playlistName}")
            }
            callback(playlists)
        }
    }

    fun uploadPlaylistToFirebase(playlist: Playlist) {
        val playlistId = database.getReference("Playlist").push().key
        val playlistRef = database.getReference("Playlist").child(playlistId!!)
        playlist.playlsitId = playlistId
        playlistRef.setValue(playlist)
            .addOnSuccessListener {
                Log.d("FirebasePlaylist", "Playlist uploaded successfully with ID: ${playlist.playlsitId}")
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error uploading playlist: ${e.message}")
            }
    }

    fun updatePlaylistInFirebase(playlist: Playlist, callback: (Boolean) -> Unit) {
        val playlistRef = database.getReference("Playlist").child(playlist.playlsitId)
        playlistRef.setValue(playlist)
            .addOnSuccessListener {
                Log.d("FirebasePlaylist", "Playlist updated successfully with ID: ${playlist.playlsitId}")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error updating playlist: ${e.message}")
                callback(false)
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

    fun fetchAllSongsFromFirebase(callback: (List<Song>) -> Unit) {
        val songRef = FirebaseDatabase.getInstance().getReference("Songs")
        songRef.get().addOnSuccessListener { snapshot ->
            Log.d("FirebaseSong", "Fetched all songs ${snapshot.value}")
            val songs = mutableListOf<Song>()
            val objectsMap = snapshot.value as Map<*, *>
            for ((_, value) in objectsMap) {
                val songMap = value as Map<*, *>
                val song = Song(
                    songMap["id"] as String,
                    songMap["songName"] as String,
                    songMap["artist"] as String,
                    songMap["album"] as String,
                    songMap["duration"] as String,
                    songMap["coverArtUrl"] as String,
                    songMap["songUrl"] as String,
                    songMap["releaseDate"] as String,
                    (songMap["numListeners"] as Long).toInt(),
                    songMap["genres"] as List<String>,
                    songMap["album"] as String,

                )
                songs.add(song)
            }
            callback(songs)
        }
    }

    fun updateSongInFirebase(song: SongNew, callback: (Boolean) -> Unit) {
        val userId = UserManager.getCurrentUser()!!.id
        val songRef = database.getReference("LikedSongs").child(userId)

        songRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val likedSongs = snapshot.value as? MutableList<String> ?: mutableListOf()
                if (!likedSongs.contains(song.songId)) {
                    likedSongs.add(song.songId)
                } else {
                    likedSongs.remove(song.songId)
                }

                songRef.setValue(likedSongs)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FirebaseSong", "Song updated successfully with ID: ${song.songId}")
                            callback(true)
                        } else {
                            Log.e(ContentValues.TAG, "Error updating song: ${task.exception?.message}")
                            callback(false)
                        }
                    }
            } else {
                Log.e(ContentValues.TAG, "Error fetching liked songs: ${task.exception?.message}")
                callback(false)
            }
        }
    }

    fun getLikedSongsFromFirebase(callback: (List<String>) -> Unit) {
        val userId = UserManager.getCurrentUser()!!.id
        val songRef = database.getReference("LikedSongs").child(userId)
        songRef.get().addOnSuccessListener { snapshot ->
            val likedSongs = snapshot.value as? List<String> ?: listOf()
            callback(likedSongs)
        }
    }

}

