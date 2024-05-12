package com.smd.surmaiya.itemClasses

data class Album(
    var id: String,
    val name: String,
    var coverArtUrl: String,
    val releaseDate: String,
    val songIds: MutableList<String>,
    val songs: List<Song>?,
    val artists: List<String>
){
    constructor(): this("","","","", mutableListOf(), mutableListOf(), mutableListOf())
}
