package com.smd.surmaiya.itemClasses

data class SongNew(
    val songCoverImageResource: String,
    val songName: String,
    val artistName: String,
    var isSelected: Boolean = false,
    var isLiked: Boolean = false,
    var songId:String,
) {
    constructor(coverArtUrl: String, songName: String, artist: String, id: String) : this(coverArtUrl, songName, artist, false, false, id)
    constructor(coverArtUrl: String, songName: String, artist: String, id: String, isLiked: Boolean) : this(coverArtUrl, songName, artist,false, isLiked, id)

}