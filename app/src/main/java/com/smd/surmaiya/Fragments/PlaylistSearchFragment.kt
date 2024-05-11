package com.smd.surmaiya.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager.fetchSongFromFirebase
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaylistSearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var editPlaylist: ImageView
    private lateinit var playlistSearch: ImageView
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: AlbumAddSongAdapter
    private lateinit var playlistCover: ShapeableImageView
    private lateinit var playlistName: TextView
    private lateinit var followers: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_playlist_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val playlist = getPlaylist()
        setupPlaylistDetails(playlist)
        setupSongsListForPlaylist(playlist)
    }

    private fun getPlaylist(): Playlist? {
        return PlaylistManager.getPlaylists()
    }

    private fun setupPlaylistDetails(playlist: Playlist?) {
        if (playlist != null) {
            playlistCover = view?.findViewById(R.id.playlistCover)!!
            playlistName = view?.findViewById(R.id.playlistName)!!
            followers = view?.findViewById<TextView>(R.id.followers)!!

            Glide.with(this)
                .load(playlist.coverArtUrl)
                .into(playlistCover)
            playlistName.text = playlist.playlistName
            "${playlist.followers} Followers".also { followers.text = it }
        }
    }

    private fun setupSongsListForPlaylist(playlist: Playlist?) {
        val songsList = mutableListOf<Song>()
        val songIds = extractSongIdsFromPlaylist(playlist)

        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
        playlistRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        for (songId in songIds) {
            fetchSongFromFirebase(songId.toString()) { song ->
                songsList.add(song)
                val newSongsList = createNewSongsList(songsList)
                setupAdapter(newSongsList)
            }
        }
    }

    private fun createNewSongsList(songsList: MutableList<Song>): MutableList<SongNew> {
        val newSongsList = mutableListOf<SongNew>()
        for (songs in songsList) {
            newSongsList.add(SongNew(songs.coverArtUrl,songs.songName,songs.artist))
        }
        return newSongsList
    }

    private fun setupAdapter(newSongsList: MutableList<SongNew>) {
        playlistAdapter = AlbumAddSongAdapter(newSongsList)
        playlistRecyclerView.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()
    }

    private fun extractSongIdsFromPlaylist(playlist: Playlist?): List<Any> {
        return playlist?.songids?.values?.toList() ?: listOf()
    }


    private lateinit var backButton: ImageView
    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        editPlaylist = view?.findViewById(R.id.editPlaylist)!!
    }

    fun setUpOnClickListeners() {

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        editPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditPlaylistFragment()).addToBackStack(null).commit()
        }

    }
        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaylistSearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaylistSearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}