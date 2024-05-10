package com.smd.surmaiya.itemClasses

data class Playlist(
    val playlsitId: String,
    val playlistName: String,
    val songids: Map<String, Any>,
    val coverArtUrl: String,
    val userIds: Map<String, Any>,
    val dateAdded: String,
    val followers: Long,
    val visibility: String
) {
    constructor() : this("", "", mapOf(), "", mapOf(), "10/5/2024", 0, "")
}