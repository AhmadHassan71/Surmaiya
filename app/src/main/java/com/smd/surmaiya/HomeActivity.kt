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
        prepareTopAlbums()
        prepareTopPlaylists()

    }

    private fun prepareTopAlbums() {
        val recyclerView: RecyclerView =
            findViewById(R.id.topAlbumsRecyclerView) // Find your RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val listData = prepareListData() // Replace with your data loading logic
        val listItemAdapter = ListItemAdapter(listData)
        recyclerView.adapter = listItemAdapter
    }

    private fun prepareTopPlaylists(){
        val recyclerView: RecyclerView = findViewById(R.id.topPlaylistsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val listData = prepareListData()
        val listItemAdapter = ListItemAdapter(listData)
        recyclerView.adapter = listItemAdapter
    }

    private fun prepareListData(): List<ListItem> {
        val listItems = mutableListOf<ListItem>()
        // Add your list items here
        listItems.add(ListItem(R.drawable.playlist, "Primary Text", "Secondary Text"))
        // ... add more list items
        return listItems
    }


    private fun prepareTopGenres() {
        val recyclerView: RecyclerView = findViewById(R.id.topGenresRecyclerView)

        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val genreDataList = prepareGenreData()
        val genreAdapter = TopGenresAdapter(genreDataList)
        recyclerView.adapter = genreAdapter
    }

    private fun prepareGenreData(): List<TopGenres> {
        val genres = mutableListOf<TopGenres>()
        genres.add(TopGenres(R.drawable.genre_image, "Rock"))
        genres.add(TopGenres(R.drawable.genre_image, "Pop"))
        genres.add(TopGenres(R.drawable.genre_image, "Metal"))
        genres.add(TopGenres(R.drawable.genre_image, "R&B"))

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