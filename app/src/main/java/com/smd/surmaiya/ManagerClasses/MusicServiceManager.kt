package com.smd.surmaiya.ManagerClasses

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.IBinder
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
        Log.d("playSong musicservice manager", "playSong: , ${song.songUrl.toString()}")
        musicService?.playSong(song)
        songManager.currentSong = song

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        musicService?.sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun pauseSong() {
        songManager.currentProgress = (musicService?.getProgress() ?: 0).toFloat()
        musicService?.pauseMusic()

        val intent = Intent("com.smd.surmaiya.ACTION_PAUSE")
        musicService?.sendBroadcast(intent)
    }

    fun resumeSong() {

        songManager.currentProgress = (musicService?.getProgress() ?: 0).toFloat()
        musicService?.resumeSong()

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        musicService?.sendBroadcast(intent)
    }

    fun getService(): MusicService? {
        return musicService
    }
}