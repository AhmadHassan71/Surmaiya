package com.smd.surmaiya.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.TopSongsAdapter
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song

class MonthlyRankingsFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var monthlyRankingRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monthly_ranking, container, false)

        initializeViews(view)
        prepareMonthlyRanking()
        setUpOnClickListeners()

        return view
    }

    fun initializeViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        monthlyRankingRecyclerView = view.findViewById(R.id.monthlyRankingRecyclerView)
    }

    fun setUpOnClickListeners() {
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun prepareMonthlyRanking() {
        monthlyRankingRecyclerView.layoutManager = LinearLayoutManager(context)

        val songData = prepareSongData()
        val songAdapter = TopSongsAdapter(songData,true, playlists = listOf<Playlist>())
        monthlyRankingRecyclerView.adapter = songAdapter
    }

    private fun prepareSongData(): List<Song> {
        val songs = mutableListOf<Song>()
        // Add your songs here
        // ... add more songs
        return songs
    }
}