package com.smd.surmaiya.itemClasses

data class SongNew(
    val songCoverImageResource: String,
    val songName: String,
    val artistName: String
) {
    constructor() : this("", "", "")
}