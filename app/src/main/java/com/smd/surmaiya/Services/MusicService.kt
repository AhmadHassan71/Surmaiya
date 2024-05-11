package com.smd.surmaiya.Services

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song


class MusicService : Service() {

    private var exoPlayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

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

    fun playSong(songUri: Uri) {
        try {
            Log.d("playSong musicservice", "playSong: , ${songUri.toString()}")
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
            val mediaItem = MediaItem.fromUri(songUri)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()

            Log.d("playSong musicservice", "playSong: , ${exoPlayer?.isPlaying}")
            Log.d("playSong musicservice", "playSong: , ${exoPlayer?.playWhenReady}")
        } catch (e: Exception) {
            Log.e("playSong musicservice", "Error playing song: ${e.message}")
        }
    }

    fun showNotification(song: Song, albumArtBitmap: Bitmap) {
        Log.d("MusicService", "showNotification: Called")

        val notificationManager = NotificationManagerCompat.from(this)
        if (!notificationManager.areNotificationsEnabled()) {
            Log.w("MusicService", "Cannot show notification: notifications are disabled for the app.")
            return
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2) // Show the play, pause, next and previous buttons in the compact notification view.

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.music_channel_id))
            .setSmallIcon(R.drawable.logo_home) // Replace with your own icon
            .setContentTitle(song.songName)
            .setContentText(song.artist)
            .setLargeIcon(albumArtBitmap) // Replace with the album art bitmap
            .setStyle(mediaStyle)

        val playIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pauseIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val previousIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.previous, "Previous", previousIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.pause, "Pause", pauseIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.player_play, "Play", playIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.next, "Next", nextIntent))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("MusicService", "Cannot show notification: the app does not have the POST_NOTIFICATIONS permission.")
            return
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        Log.d("MusicService", "showNotification: Notification shown")
    }





    fun pauseMusic() {
        exoPlayer?.playWhenReady = false
    }

    fun resumeSong() {
        exoPlayer?.playWhenReady = true
    }

    fun getProgress(): Int {
        val duration = exoPlayer?.duration ?: 0
        val position = exoPlayer?.currentPosition ?: 0
        return ((position * 100 / duration)/2).toInt()
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