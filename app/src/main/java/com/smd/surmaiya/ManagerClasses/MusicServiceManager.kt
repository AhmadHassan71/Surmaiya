package com.smd.surmaiya.ManagerClasses

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.smd.surmaiya.Services.MusicService
import com.smd.surmaiya.itemClasses.Song

object MusicServiceManager {

    private var musicService: MusicService? = null
    private var isBound = false
    private lateinit var serviceConnection: ServiceConnection

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

    fun showNotification(song: Song, albumArtBitmap: Bitmap) {
        musicService?.showNotification(song, albumArtBitmap)
    }


    fun playSong(uri: Uri) {
        Log.d("playSong musicservice manager", "playSong: , ${uri.toString()}")
        musicService?.playSong(uri)
    }

    fun pauseSong() {
        musicService?.pauseMusic()
    }

    fun resumeSong() {
        musicService?.resumeSong()
    }

    fun getService(): MusicService? {
        return musicService
    }
}