package com.smd.surmaiya.ManagerClasses

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.smd.surmaiya.Services.MusicService

object MusicServiceManager {

    private var musicService: MusicService? = null
    private var isBound = false
    private lateinit var serviceConnection: ServiceConnection
    private var onServiceConnected: (() -> Unit)? = null

    fun bindService(context: Context, onServiceConnected: (() -> Unit)? = null) {
        this.onServiceConnected = onServiceConnected
        if (!isBound) {
            serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as MusicService.MusicBinder
                    musicService = binder.getService()
                    isBound = true
                    this@MusicServiceManager.onServiceConnected?.invoke()
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


    fun playSong(uri: Uri) {
        Log.d("playSong musicservice manager", "playSong: , ${uri.toString()}")
        musicService?.playSong(uri)
    }
    fun getService(): MusicService? {
        return musicService
    }
}