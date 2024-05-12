package com.smd.surmaiya.ManagerClasses

import com.smd.surmaiya.itemClasses.Genre

object GenreManager {
    private var genre: Genre? = null

    fun setGenre(newGenre: Genre) {
        genre = newGenre

    }

    fun getGenre(): Genre? {
        return genre
    }

    fun removeGenre() {
        genre = null
    }
}