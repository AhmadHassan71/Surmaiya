package com.smd.surmaiya.itemClasses
import android.os.Parcelable
import  kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(
    var playlsitId: String,
    var playlistName: String,
    var songIds: List<String>,
    var coverArtUrl: String,
    var userIds: List<String>,
    val dateAdded: List<String>,
    var followers: Long,
    var followerIds: List<String>,
    var visibility: String,
    var playlistDescription: String

): Parcelable {
    constructor() : this("", "", listOf(), "", listOf(), listOf(), 0, listOf(), "","")
}