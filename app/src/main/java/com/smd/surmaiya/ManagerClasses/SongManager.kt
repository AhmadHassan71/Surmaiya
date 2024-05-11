package com.smd.surmaiya.ManagerClasses

import com.smd.surmaiya.itemClasses.Song

class SongManager private constructor() {
    var currentSong: Song? = null
    var currentProgress: Float = 0.0f
    var songQueue: MutableList<Song> = mutableListOf()

    fun addToQueue(song: Song) {
        songQueue.add(song)
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