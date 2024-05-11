package com.smd.surmaiya.itemClasses

data class Playlist(
    var playlsitId: String,
    var playlistName: String,
    var songIds: List<String>,
    var coverArtUrl: String,
    val userIds: List<String>,
    val dateAdded: List<String>,
    val followers: Long,
    val followerIds: List<String>,
    var visibility: String,
    var playlistDescription: String

) {
    constructor() : this("", "", listOf(), "", listOf(), listOf(), 0, listOf(), "","")
}