package com.smd.surmaiya.ManagerClasses

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.smd.surmaiya.Services.MusicService
import com.smd.surmaiya.itemClasses.Song

object MusicServiceManager {

    var musicService: MusicService? = null
    private var isBound = false
    private lateinit var serviceConnection: ServiceConnection
    private val songManager = SongManager.getInstance()

    fun bindService(context: Context) {
        if (!isBound) {
            serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as MusicService.MusicBinder
                    musicService = binder.getService()
                    isBound = true
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    isBound = false
                }
            }

            Intent(context, MusicService::class.java).also { intent ->
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun unbindService(context: Context) {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun showNotification(song: Song, albumArtBitmap: Bitmap) {
        musicService?.showNotification(song, albumArtBitmap)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun playSong(song: Song) {
        // Check if the current song is the same as the song to be played and if it is already playing
        if (songManager.currentSong == song && musicService?.exoPlayer?.isPlaying == true) {
            // If the song is already playing, do nothing
            return
        }

        Log.d("playSong musicservice manager", "playSong: , ${song.songUrl.toString()}")
        musicService?.playSong(song)
        songManager.currentSong = song

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        musicService?.sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun playCurrentSongWithDelay(delayMillis: Long) {
        val progress = musicService?.getProgress()?.toFloat() ?: 0f
        SongManager.getInstance().currentSong?.let { song ->
            playMusicWithDelay(song, progress, delayMillis)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun playMusicWithDelay(song: Song, progress: Float, delayMillis: Long) {
        getService()?.playSong(song, progress)

        Handler(Looper.getMainLooper()).postDelayed({
            val duration = getService()?.exoPlayer?.duration ?: 0
            Log.d("Progress percentage= ", "Progress percentage = " + progress/100)
            Log.d("DUration = ", "DUration = " + duration)
            Log.d("Progress = ", "Progress = " + progress)
            val newProgress = (progress/ 100.0) * duration
            getService()?.exoPlayer?.seekTo(newProgress.toLong())
        }, delayMillis) // Delay of 1 second

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        musicService?.sendBroadcast(intent)
    }

    fun pauseMusicAndBroadcast() {
        musicService?.progress = musicService?.getProgress()?.toFloat() ?: 0f
        musicService?.exoPlayer?.stop()

        val intent = Intent("com.smd.surmaiya.ACTION_PAUSE")
        musicService?.sendBroadcast(intent)
    }


    fun getService(): MusicService? {
        return musicService
    }
}