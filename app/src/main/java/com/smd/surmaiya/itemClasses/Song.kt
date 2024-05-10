package com.smd.surmaiya.itemClasses


data class Song(
    val id: String,
    val songName: String,
    val artist:String,
    val album: String,
    val duration: String,
    var coverArtUrl:String,
    var songUrl: String,
    val releaseDate: String,
    val numListeners: Int,
    val genres: List<String>
)