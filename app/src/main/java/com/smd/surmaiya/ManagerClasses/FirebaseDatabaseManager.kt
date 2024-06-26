package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
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
//            database.setPersistenceEnabled(true)
        }
        return instance!!
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }


    fun signUpUser(user: User, activity: AppCompatActivity, Callback: (Boolean) -> Unit) {

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
                    playlistMap["playlistDescription"] as String,
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
                Log.d(
                    "FirebasePlaylist",
                    "Playlist uploaded successfully with ID: ${playlist.playlsitId}"
                )
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error uploading playlist: ${e.message}")
            }
    }

    fun updatePlaylistInFirebase(playlist: Playlist, callback: (Boolean) -> Unit) {
        val playlistRef = database.getReference("Playlist").child(playlist.playlsitId)
        playlistRef.setValue(playlist)
            .addOnSuccessListener {
                Log.d(
                    "FirebasePlaylist",
                    "Playlist updated successfully with ID: ${playlist.playlsitId}"
                )
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

    fun getAllGenres(callback: (List<String>) -> Unit) {
        val myRef = database.getReference("Genres")
        val genreList = mutableListOf<String>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val genre = postSnapshot.key
                    if (genre != null) {
                        genreList.add(genre)
                    }
                }
                callback(genreList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching genres: ${databaseError.message}")
            }
        })
    }

    fun getAllSongs(callback: (List<Song>) -> Unit) {
        val myRef = database.getReference("Songs")
        val songList = mutableListOf<Song>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val song = postSnapshot.getValue(Song::class.java)
                    if (song != null) {
                        songList.add(song)
                    }
                }
                callback(songList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching songs: ${databaseError.message}")
            }
        })
    }

    fun getPlaylistsByUserId(userId: String, callback: (List<Playlist>) -> Unit) {
        val playlistsRef = database.getReference("Playlist")
        playlistsRef.get().addOnSuccessListener { snapshot ->
            val playlists = mutableListOf<Playlist>()
            val objectsMap = snapshot.value as Map<*, *>
            for ((_, value) in objectsMap) {
                val playlistMap = value as Map<*, *>
                val playlistUserIds = playlistMap["userIds"] as List<String>
                if (playlistUserIds.contains(userId)) {
                    val playlist = Playlist(
                        playlistMap["playlsitId"] as String,
                        playlistMap["playlistName"] as String,
                        playlistMap["songIds"] as List<String>,
                        playlistMap["coverArtUrl"] as String,
                        playlistUserIds,
                        playlistMap["dateAdded"] as List<String>,
                        playlistMap["followers"] as Long,
                        playlistMap["followerIds"] as List<String>,
                        playlistMap["visibility"] as String,
                        playlistMap["playlistDescription"] as String
                    )
                    playlists.add(playlist)
                    Log.d(
                        "FirebasePlaylist",
                        "Fetched playlist with ID: ${playlist.playlsitId} for user ID: $userId"
                    )
                }
            }
            callback(playlists)
        }.addOnFailureListener { exception ->
            Log.e("FirebasePlaylist", "Failed to fetch playlists for user ID: $userId", exception)
            callback(emptyList())
        }
    }

    fun getUsersByIds(userIds: List<String>, callback: (List<User>) -> Unit) {
        val usersRef = database.getReference("users")
        val users = mutableListOf<User>()
        for (userId in userIds) {
            usersRef.child(userId).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    users.add(user)
                    if (users.size == userIds.size) {
                        callback(users)
                    }
                } else {
                    Log.d("FirebaseUser", "No user found with ID: $userId")
                }
            }.addOnFailureListener { exception ->
                Log.d("FirebaseUser", "Failed to fetch user with ID: $userId", exception)
            }

        }
    }

    fun followUser(currentUser: User, otherUser: User, callback: (Boolean) -> Unit) {
        // Add the other user's ID to the current user's following list
        currentUser.following.add(otherUser.id)

        // Add the current user's ID to the other user's followers list
        otherUser.followers.add(currentUser.id)

        // Update the current user in the database
        updateUser(currentUser) { success ->
            if (success) {
                // If the current user was updated successfully, update the other user
                updateUser(otherUser) { success ->
                    callback(success)
                }
            } else {
                callback(false)
            }
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
                            Log.d(
                                "FirebaseSong",
                                "Song updated successfully with ID: ${song.songId}"
                            )
                            callback(true)
                        } else {
                            Log.e(
                                ContentValues.TAG,
                                "Error updating song: ${task.exception?.message}"
                            )
                            callback(false)
                        }
                    }
            } else {
                Log.e(
                    ContentValues.TAG,
                    "Error fetching liked songs: ${task.exception?.message}"
                )
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

    fun unfollowUser(currentUser: User, otherUser: User, callback: (Boolean) -> Unit) {
        // Remove the other user's ID from the current user's following list
        currentUser.following.remove(otherUser.id)

        // Remove the current user's ID from the other user's followers list
        otherUser.followers.remove(currentUser.id)

        // Update the current user in the database
        updateUser(currentUser) { success ->
            if (success) {
                // If the current user was updated successfully, update the other user
                updateUser(otherUser) { success ->
                    callback(success)
                }
            } else {
                callback(false)
            }
        }
    }

    fun getAllAlbums(callback: (List<Album>) -> Unit) {
        val myRef = database.getReference("Albums")
        val albumList = mutableListOf<Album>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val album = postSnapshot.getValue(Album::class.java)
                    if (album != null) {
                        albumList.add(album)
                    }
                }
                callback(albumList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching albums: ${databaseError.message}")
            }
        })
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        val myRef = database.getReference("users")
        val userList = mutableListOf<User>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                callback(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching users: ${databaseError.message}")
            }
        })
    }



    fun addNotificationToUser(
        userId: String,
        notification: String,
        notificationType: String,
    ) {
        val database = FirebaseDatabase.getInstance()
        val notificationRef =
            database.getReference("users").child(userId).child("notifications").push()

        notificationRef.setValue(
            mapOf(
                "notification" to notification,
                "notificationType" to notificationType,
            )
        )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Notification added successfully")

            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Failed to add notification: ${e.message}")
            }


    }



    fun sendNotificationsToListOfUsers(
        userIds: List<String>,
        notification: String,
        notificationType: String,
    ) {
        userIds.forEach { userId ->
            addNotificationToUser(userId, notification, notificationType)
        }
    }

    fun addSongToRecentlyPlayed(song: Song) {
        val userId = UserManager.getCurrentUser()?.id ?: return
        val recentlyPlayedRef = database.getReference("users").child(userId).child("recentlyPlayed")
        val key = recentlyPlayedRef.push().key ?: return
        recentlyPlayedRef.child(key).setValue(song)
    }

    fun getRecentlyPlayedSongs(callback: (List<Song>) -> Unit) {
        val userId = UserManager.getCurrentUser()?.id ?: return
        val recentlyPlayedRef = database.getReference("users").child(userId).child("recentlyPlayed")
        recentlyPlayedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FirebaseDatabaseManager", "Fetched recently played songsn")
                Log.d("FirebaseDatabaseManager", "DataSnapshot: $dataSnapshot")
                val recentlyPlayedSongs = dataSnapshot.children.mapNotNull { it.getValue(Song::class.java) }
                callback(recentlyPlayedSongs)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }




}

