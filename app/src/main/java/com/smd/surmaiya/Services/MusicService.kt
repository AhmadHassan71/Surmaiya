package com.smd.surmaiya.Services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class MusicService : Service() {

    private var exoPlayer: SimpleExoPlayer? = null

    override fun onBind(intent: Intent): IBinder {
        return MusicBinder()
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
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




    fun pauseMusic() {
        exoPlayer?.playWhenReady = false
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
    }

}