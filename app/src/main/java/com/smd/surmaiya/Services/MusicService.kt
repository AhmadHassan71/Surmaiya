package com.smd.surmaiya.Services

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.smd.surmaiya.ManagerClasses.SongManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song
import android.content.Context
import com.google.firebase.storage.FirebaseStorage

import java.io.File
import java.io.FileInputStream


class MusicService : Service() {

    var exoPlayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val songManager = SongManager.getInstance()
    private var albumArtBitmap: Bitmap? = null
    var progress = 0f

    override fun onBind(intent: Intent): IBinder {
        return MusicBinder()
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
        mediaSession?.isActive = true

    }

    private val handler = Handler(Looper.getMainLooper())




    @RequiresApi(Build.VERSION_CODES.P)
    fun playSong(song: Song,progresss:Float=0f) {
        try {
            exoPlayer?.release()
            exoPlayer = SimpleExoPlayer.Builder(this).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == Player.STATE_READY && playWhenReady) {
                            // The player is ready to play and is playing
                        } else if (playbackState == Player.STATE_READY) {
                            // The player is ready to play but is paused
                            this@apply.playWhenReady = true
                        }
                    }
                })
            }

            val musicCache = MusicCache(this)
            val cachedFile = musicCache.getCachedFile(song.songUrl)

            val mediaItem = if (cachedFile != null) {
                // If the song is in the cache, use the cached file
                MediaItem.fromUri(Uri.fromFile(cachedFile))
            } else {
                // If the song is not in the cache, download and cache the song
                musicCache.cacheSong(song.songUrl)?.let { cachedSong ->
                    MediaItem.fromUri(Uri.fromFile(cachedSong))
                } ?: MediaItem.fromUri(song.songUrl)
            }

            SongManager.getInstance().currentSong = song
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()

            //wait until the player is ready
            exoPlayer?.playWhenReady = true

            Log.d("playSong musicservice", "progress is $progresss")
            if(progresss!=0f){
                val progressPosition = (exoPlayer?.duration ?: 0L) * (progress / 100)

                Log.d("Progress percentage= ", "Progress percentage = " + progress/100)
                Log.d("DUration = ", "DUration = " + exoPlayer?.duration)
                Log.d("playSong musicservice", "progressPosition is $progressPosition")
                Log.d("playsong musicserive ", "progress is $progress")
                exoPlayer?.seekTo(progressPosition.toLong())
            }

            exoPlayer?.play()

            val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
            sendBroadcast(intent)
            showNotification(song, albumArtBitmap ?: return)


        } catch (e: Exception) {
            Log.e("playSong musicservice", "Error playing song: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun pauseMusic() {
        SongManager.getInstance().currentProgress = (exoPlayer?.currentPosition ?: 0f).toFloat()
        exoPlayer?.stop()

        val intent = Intent("com.smd.surmaiya.ACTION_PAUSE")
        sendBroadcast(intent)
    }

    fun resumeSong() {
        exoPlayer?.playWhenReady = true
        SongManager.getInstance().currentProgress = (exoPlayer?.currentPosition ?: 0f).toFloat()

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        sendBroadcast(intent)
    }

    fun getProgress(): Int {
        val duration = exoPlayer?.duration ?: 0
        val position = exoPlayer?.currentPosition ?: 0

        val progress = (position * 100 / duration).toInt()
        SongManager.getInstance().currentProgress = progress.toFloat()
        return progress
    }

        @RequiresApi(Build.VERSION_CODES.P)
    fun showNotification(song: Song, albumArtBitmap: Bitmap) {
        this.albumArtBitmap = albumArtBitmap

        val notificationManager = NotificationManagerCompat.from(this)
        if (!notificationManager.areNotificationsEnabled()) {
            Log.w("MusicService", "Cannot show notification: notifications are disabled for the app.")
            return
        }

        val mediaMetadataBuilder = MediaMetadataCompat.Builder()
        mediaMetadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exoPlayer?.duration ?: 0)

        mediaSession?.setMetadata(mediaMetadataBuilder.build())
        val currentPosition = exoPlayer?.currentPosition?.toFloat() ?: 0f
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build()

        mediaSession?.setPlaybackState(playbackState)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.music_channel_id))
            .setSmallIcon(R.drawable.logo_home)
            .setStyle(mediaStyle)
            .setContentText(song.artist)
            .setContentTitle(song.songName)
            .setLargeIcon(albumArtBitmap)

        val playIntent = Intent("com.smd.surmaiya.ACTION_PLAY")
        val pauseIntent = Intent("com.smd.surmaiya.ACTION_PAUSE")

            val pendingPlayIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(this, REQUEST_CODE, playIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getBroadcast(this, REQUEST_CODE, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val pendingPauseIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(this, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getBroadcast(this, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            notificationBuilder.addAction(NotificationCompat.Action(R.drawable.pause, "Pause", pendingPauseIntent))
            notificationBuilder.addAction(NotificationCompat.Action(R.drawable.player_play, "Play", pendingPlayIntent))


        val nextIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val previousIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.previous, "Previous", previousIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.next, "Next", nextIntent))

        // Display the notification without starting the service in the foreground
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }





    fun stopMusic() {
        exoPlayer?.stop()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        mediaSession?.release()
    }

    companion object {
        const val REQUEST_CODE = 0
        const val ACTION_PLAY = "com.smd.surmaiya.ACTION_PLAY"
        const val ACTION_PAUSE = "com.smd.surmaiya.ACTION_PAUSE"
        const val ACTION_NEXT = "com.smd.surmaiya.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.smd.surmaiya.ACTION_PREVIOUS"
        const val CHANNEL_ID = "com.smd.surmaiya.MUSIC_CHANNEL"
        const val NOTIFICATION_ID = 1
    }

}

class MusicCache(private val context: Context) {

    private val cacheDirectory: File by lazy {
        File(context.cacheDir, "music_cache").apply { mkdirs() }
    }

    fun getCachedFile(songUrl: String): File? {
        val cachedFileName = getCacheFileName(songUrl)
        return File(cacheDirectory, cachedFileName).takeIf { it.exists() }
    }

    fun cacheSong(songUrl: String): File? {
        val cachedFileName = getCacheFileName(songUrl)
        val cachedFile = File(cacheDirectory, cachedFileName)
        if (cachedFile.exists()) {
            Log.d("MusicCache", "Song already cached: $cachedFileName")
            return cachedFile
        }

        try {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(songUrl)
            val localFile = File.createTempFile("songs", "mp3")
            storageReference.getFile(localFile).addOnSuccessListener {
                FileInputStream(localFile).use { input ->
                    cachedFile.outputStream().use { output ->
                        input.copyTo(output)
                        Log.d("MusicCache", "Song cached successfully: $cachedFileName")
                    }
                }
            }.addOnFailureListener {
                Log.e("MusicCache", "Error caching song: ${it.message}")
            }
        } catch (e: Exception) {
            Log.e("MusicCache", "Error caching song: ${e.message}")
        }
        return null
    }

    private fun getCacheFileName(songUrl: String): String {
        return songUrl.substringAfterLast("/")
    }
}