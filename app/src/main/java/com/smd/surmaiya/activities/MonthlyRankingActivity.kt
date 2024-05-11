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

        initializeViews()
        prepareMonthlyRanking()
        setUpOnClickListeners()


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
//        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre")))
//        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre")))
//        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre")))
//        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre")))


        // ... add more songs
        return songs
    }

}