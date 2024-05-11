package com.smd.surmaiya.itemClasses

data class Playlist(
    var playlsitId: String,
    val playlistName: String,
    val songIds: List<String>,
    val coverArtUrl: String,
    val userIds: List<String>,
    val dateAdded: List<String>,
    val followers: Long,
    val visibility: String
) {
    constructor() : this("", "", listOf(), "", listOf(), listOf(), 0, "")
}