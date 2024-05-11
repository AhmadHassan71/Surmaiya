package com.smd.surmaiya.ManagerClasses

import com.smd.surmaiya.itemClasses.Playlist

object PlaylistManager {
    private var playlists : Playlist? = null

    fun addPlaylist(playlist: Playlist) {
        playlists = playlist
    }
    fun getPlaylists(): Playlist? {
        return playlists
    }
    fun removePlaylist() {
        playlists = null
    }

}