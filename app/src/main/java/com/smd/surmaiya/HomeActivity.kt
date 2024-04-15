package com.smd.surmaiya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // Assuming you have a layout with RecyclerView
        prepareRecentlyPlayed()
        prepareTopGenres()

    }

    private fun prepareTopGenres() {
        val recyclerView: RecyclerView = findViewById(R.id.topGenresRecyclerView)

        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val genreDataList = prepareGenreData()
        val genreAdapter = TopGenresAdapter(genreDataList)
        recyclerView.adapter = genreAdapter
    }

    private fun prepareGenreData(): List<TopGenres> {
        val genres = mutableListOf<TopGenres>()
        genres.add(TopGenres(R.drawable.genre_image, "Rock"))
        genres.add(TopGenres(R.drawable.genre_image, "Pop"))
        return genres
    }

    private fun prepareRecentlyPlayed() {
        val recyclerView: RecyclerView = findViewById(R.id.recentlyPlayedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val songList = prepareSongData()  // Replace with your data loading logic
        val songAdapter = RecentlyPlayedAdapter(songList)
        recyclerView.adapter = songAdapter
    }

    private fun prepareSongData(): List<RecentlyPlayedSong> {
        val songs = mutableListOf<RecentlyPlayedSong>()
        songs.add(RecentlyPlayedSong(R.drawable.album, "Dawn FM", "Weekend"))
        songs.add(RecentlyPlayedSong(R.drawable.album, "Dawn FM", "Weekend"))
        songs.add(RecentlyPlayedSong(R.drawable.album, "Dawn FM", "Weekend"))

        return songs
    }
}