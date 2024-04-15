package com.smd.surmaiya.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.PopularPlaylistAdapter
import com.smd.surmaiya.itemClasses.Playlist

class PopularPlaylistsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_playlists) // Assuming you have a layout with RecyclerView

        preparePopularPlaylists()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun preparePopularPlaylists() {
        val recyclerView: RecyclerView = findViewById(R.id.popularPlaylistsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val playlistData = preparePlaylistData()
        val playlistAdapter = PopularPlaylistAdapter(playlistData)
        recyclerView.adapter = playlistAdapter
    }

    private fun preparePlaylistData(): List<Playlist> {
        val playlists = mutableListOf<Playlist>()
        // Add your playlists here
        playlists.add(Playlist(R.drawable.popular_playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.popular_playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.popular_playlist, "Playlist Name", 80))
        playlists.add(Playlist(R.drawable.popular_playlist, "Playlist Name", 80))

        // ... add more playlists
        return playlists
    }

}
