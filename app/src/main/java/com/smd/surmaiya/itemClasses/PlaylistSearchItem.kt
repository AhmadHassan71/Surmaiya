package com.smd.surmaiya.itemClasses

data class PlaylistSearchItem(
    val imageUrl: String, val title: String, val subtitle: String
) {
    override fun toString(): String {
        return "PlaylistSearchItem(imageUrl='$imageUrl', title='$title', subtitle='$subtitle')"
    }
}