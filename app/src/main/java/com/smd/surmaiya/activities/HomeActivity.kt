package com.smd.surmaiya.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.adapters.ListItemAdapter
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.adapters.RecentlyPlayedAdapter
import com.smd.surmaiya.adapters.TopGenresAdapter
import com.smd.surmaiya.itemClasses.ListItem
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.TopGenres
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.TopSongsAdapter

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // Assuming you have a layout with RecyclerView

        prepareRecentlyPlayed()

        prepareTopGenres()

        prepareYourPlaylists()

        prepareTopAlbums()

        prepareTopPlaylists()

        val topGenresTextView = findViewById<TextView>(R.id.topGenresTextView)
        topGenresTextView.setOnClickListener {
            val intent = Intent(this, MonthlyRankingActivity::class.java)
            startActivity(intent)
        }

        val topPlaylistTextView = findViewById<TextView>(R.id.topPlaylistsTextView)
        topPlaylistTextView.setOnClickListener {
            val intent = Intent(this, PopularPlaylistsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun prepareYourPlaylists() {
        val recyclerView: RecyclerView = findViewById(R.id.yourPlaylistsRecyclerView) // Find your RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val playlistData = preparePlaylistData()  // Replace with your data loading logic
        val playlistAdapter = PlaylistAdapter(playlistData)
        recyclerView.adapter = playlistAdapter
    }

    private fun preparePlaylistData(): List<Playlist> {
        val playlists = mutableListOf<Playlist>()
        // Add your playlists here
        playlists.add(Playlist(R.drawable.playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.playlist, "Playlist Name", 80 ))

        // ... add more playlists
        return playlists
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

    private fun prepareSongData(): List<Song> {
        val songs = mutableListOf<Song>()
        songs.add(Song(R.drawable.album, "Dawn FM", "Weekend"))
        songs.add(Song(R.drawable.album, "Dawn FM", "Weekend"))
        songs.add(Song(R.drawable.album, "Dawn FM", "Weekend"))

        return songs
    }
}