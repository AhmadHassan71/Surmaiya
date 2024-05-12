package com.smd.surmaiya.ManagerClasses

import com.smd.surmaiya.itemClasses.Album

object AlbumManager {
    private var albums: Album? = null

    fun addAlbum(album: Album) {
        albums = (album)
    }


    fun getAlbum(): Album {
        return albums!!
    }

    fun clearAlbums() {
        albums = null
    }
}