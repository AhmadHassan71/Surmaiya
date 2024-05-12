package com.smd.surmaiya.ManagerClasses

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.smd.surmaiya.itemClasses.Song

class SongManager private constructor() {
    var currentSong: Song? = null
    var currentProgress: Float = 0.0f
    var songQueue: MutableList<Song> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.P)
    fun addToQueue(song: Song) {
        songQueue.add(song)

        Log.d("SongManager", "Added song to queue")


        Log.d("SongManager", "Playing song")
        Log.d("SongManager", "Song name: ${song.songName}")
        Log.d("SongManager", "Song artist: ${song.songUrl}")
        MusicServiceManager.playSong(song)


    }

    fun addSongsFromPlaylistToQueue(songs: MutableList<Song>) {
        songQueue= songs.toMutableList()
    }

    fun removeFromQueue(song: Song) {
        songQueue.remove(song)
    }

    fun nextSong(): Song? {
        if (songQueue.isNotEmpty()) {
            currentSong = songQueue.removeAt(0)
        } else {
            currentSong = null
        }
        return currentSong
    }

    fun addAllToQueue(songs: List<Song>) {
        songQueue.addAll(songs)
    }

    fun clearQueue() {
        songQueue.clear()
    }

    fun previousSong(): Song? {
        val currentIndex = songQueue.indexOf(currentSong)
        return if (currentIndex > 0) {
            currentSong = songQueue[currentIndex - 1]
            currentSong
        } else null
    }

    companion object {
        @Volatile
        private var INSTANCE: SongManager? = null

        fun getInstance(): SongManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SongManager()
                INSTANCE = instance
                instance
            }
        }
    }
}