package com.smd.surmaiya.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.TopSongsAdapter
import com.smd.surmaiya.itemClasses.Song

class MonthlyRankingActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var monthlyRankingRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_monthly_ranking)

        prepareMonthlyRanking()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        monthlyRankingRecyclerView = findViewById(R.id.monthlyRankingRecyclerView)
    }

    fun setUpOnClickListeners() {
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun prepareMonthlyRanking() {
        monthlyRankingRecyclerView.layoutManager = LinearLayoutManager(this)

        val songData = prepareSongData()
        val songAdapter = TopSongsAdapter(songData)
        monthlyRankingRecyclerView.adapter = songAdapter
    }

    private fun prepareSongData(): List<Song> {
        val songs = mutableListOf<Song>()
        // Add your songs here
        songs.add(Song(R.drawable.playlist_image, "Song Name", "Artist Name"))
        songs.add(Song(R.drawable.playlist_image, "Song Name", "Artist Name"))
        songs.add(Song(R.drawable.playlist_image, "Song Name", "Artist Name"))
        songs.add(Song(R.drawable.playlist_image, "Song Name", "Artist Name"))

        // ... add more songs
        return songs
    }

}