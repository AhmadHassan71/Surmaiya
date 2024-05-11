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


class MusicService : Service() {

    private var exoPlayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val songManager = SongManager.getInstance()
    private var albumArtBitmap: Bitmap? = null

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
//    private val updateProgressRunnable = object : Runnable {
//        override fun run() {
//            updateNotificationProgress()
//            handler.postDelayed(this, 1000) // Update progress every second
//        }
//    }


    fun playSong(song: Song) {
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
            val mediaItem = MediaItem.fromUri(song.songUrl)
            SongManager.getInstance().currentSong = song
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
            handler.post(updateProgressRunnable)
        } catch (e: Exception) {
            Log.e("playSong musicservice", "Error playing song: ${e.message}")
        }
    }

    fun pauseMusic() {
        exoPlayer?.playWhenReady = false
        SongManager.getInstance().currentProgress = (exoPlayer?.currentPosition ?: 0f).toFloat()
        handler.removeCallbacks(updateProgressRunnable)
    }

    fun resumeSong() {
        exoPlayer?.playWhenReady = true
        SongManager.getInstance().currentProgress = (exoPlayer?.currentPosition ?: 0f).toFloat()
        handler.post(updateProgressRunnable)
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
            .setState(PlaybackStateCompat.STATE_PLAYING, currentPosition.toLong(), 1.0f)
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build()

        mediaSession?.setPlaybackState(playbackState)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.music_channel_id))
            .setSmallIcon(R.drawable.logo_home)
            .setStyle(mediaStyle)
            .setContentText(song.songName)
            .setContentTitle(song.artist)
            .setLargeIcon(albumArtBitmap)


        val playIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pauseIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val previousIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, Intent(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.previous, "Previous", previousIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.pause, "Pause", pauseIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.player_play, "Play", playIntent))
        notificationBuilder.addAction(NotificationCompat.Action(R.drawable.next, "Next", nextIntent))

        // Add progress update logic here
        val progress = getProgress()
        notificationBuilder.setProgress(100, progress, false)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.FOREGROUND_SERVICE), REQUEST_CODE)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        handler.postDelayed(updateProgressRunnable, 5000) // Update progress every second
    }



    private val updateProgressRunnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun run() {
            songManager.currentSong?.let { showNotification(it, albumArtBitmap ?: return) }
            handler.postDelayed(this, 5000) // Update progress every second
        }
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