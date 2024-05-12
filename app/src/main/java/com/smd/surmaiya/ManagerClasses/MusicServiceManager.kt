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
import com.google.android.exoplayer2.Player
import com.smd.surmaiya.Services.MusicService
import com.smd.surmaiya.itemClasses.Song

object MusicServiceManager {

    var musicService: MusicService? = null
    private var isBound = false
    private lateinit var serviceConnection: ServiceConnection
    var isRepeatMode = false
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

    fun broadCastSongSelected(song: Song){
        val intent = Intent("com.smd.surmaiya.ACTION_SONG_SELECTED")
        intent.putExtra("song", song)
        musicService?.sendBroadcast(intent)
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
    fun playOrRestartSong() {
        if (musicService?.exoPlayer?.playbackState == Player.STATE_ENDED) {
            // If the song has ended, restart it
            playSong(songManager.currentSong ?: return)
        } else {
            playCurrentSongWithDelay(1000)
        }
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

        // Add a listener to the ExoPlayer
        musicService?.exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    // When the song ends, send a broadcast
                    val intent = Intent("com.smd.surmaiya.ACTION_SONG_ENDED")
                    musicService?.sendBroadcast(intent)

                    // If repeat mode is on, replay the song
                    if (isRepeatMode) {
                        playSong(song)

                    }
                }
            }
        })

        val intent = Intent("com.smd.surmaiya.ACTION_PLAY")
        musicService?.sendBroadcast(intent)

        FirebaseDatabaseManager.addSongToRecentlyPlayed(song)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun playNextSong() {
        val nextSong = SongManager.getInstance().nextSong()
        if (nextSong != null) {
            playThisSongInstantly(nextSong)
            broadCastSongSelected(nextSong)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun playPreviousSong() {
        Log.d("playPreviousSong", "playPreviousSong: ")
        val previousSong = SongManager.getInstance().previousSong()
        if (previousSong != null) {
            playThisSongInstantly(previousSong)
            broadCastSongSelected(previousSong)
        }
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
        SongManager.getInstance().currentProgress = musicService?.getProgress()?.toFloat() ?: 0f
        Log.d("Pausing music", "Pausing music " + musicService?.getProgress()?.toFloat())
        musicService?.progress = musicService?.getProgress()?.toFloat() ?: 0f
        musicService?.exoPlayer?.stop()

        val intent = Intent("com.smd.surmaiya.ACTION_PAUSE")
        musicService?.sendBroadcast(intent)
    }

     //if song is already playing we need to stop it and play the new song

    fun isPlaying(): Boolean {
        return musicService?.exoPlayer?.isPlaying ?: false
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun playThisSongInstantly(song: Song) {
        if (isPlaying()) {
            stopSong()
        }
        playSong(song)
    }

    fun stopSong() {
        musicService?.exoPlayer?.stop()
    }


    fun getService(): MusicService? {
        return musicService
    }
}