package com.smd.surmaiya.itemClasses

import android.os.Parcelable
import  kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: String,
    val songName: String,
    val artist:String,
    var album: String,
    val duration: String,
    var coverArtUrl:String,
    var songUrl: String,
    val releaseDate: String,
    val numListeners: Int,
    val genres: List<String>,
    var albumName: String,
    var isLiked: Boolean = false,
    var numberOfListens:Int=0
): Parcelable{
    constructor() : this("", "", "", "", "", "", "", "", 0, listOf(), "",false,0)

}