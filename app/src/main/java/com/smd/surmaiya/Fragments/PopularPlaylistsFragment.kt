package com.smd.surmaiya.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.TopSongsAdapter
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song

class PopularPlaylistsFragment: Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var monthlyRankingRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popular_playlists, container, false)

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

        //fetch playlists, get the playlistname, artist, coverArtUrl and playlistId
        val playlists = preparePlaylistData(){
            val songData = mutableListOf<Song>()
            for (playlist in it){
                songData.add(Song(playlist.playlsitId,playlist.playlistName,
                    playlist.userIds.toString(),playlist.coverArtUrl,"00:00",playlist.playlistName,playlist.playlistName,"00:00",5,listOf(),"",false,0))
            }
            val songAdapter = TopSongsAdapter(songData,false, playlistIds = it.map { it.playlsitId })
            monthlyRankingRecyclerView.adapter = songAdapter
        }

    }

    private fun preparePlaylistData(Callback: (List<Playlist>) -> Unit){
        val playlists = mutableListOf<Playlist>()

        FirebaseDatabaseManager.getInstance().getPlaylists {
            playlists.addAll(it)
            Callback(playlists)
        }


    }
}