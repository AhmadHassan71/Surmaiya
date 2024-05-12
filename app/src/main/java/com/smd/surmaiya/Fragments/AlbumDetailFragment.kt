package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.ManagerClasses.AlbumManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager.getAllSongs
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.SongManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.adapters.PlaylistAuthorAdapter
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
import com.smd.surmaiya.itemClasses.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlbumDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlbumDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: AlbumAddSongAdapter
    private lateinit var playlistCover: ShapeableImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var playlistAuthorRecyclerView: RecyclerView
    private lateinit var playlistPlayButton: ImageView
    private var songsList = mutableListOf<Song>()
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

        return inflater.inflate(R.layout.fragment_album_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()
        setupAlbumDetails(getAlbum())
        setupSongsListForAlbum(getAlbum())

    }


//    private fun setupRecyclerView() {
//        val album = getAlbum()
//        setupAlbumDetails(album)
//        playlistAuthorRecyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        playlistAuthorRecyclerView.adapter = PlaylistAuthorAdapter(listOf())
//        album?.let {
//            val albumUsers = mutableListOf<User>()
//            FirebaseDatabaseManager.getAllUsers { users ->
//                for (user in users) {
//                    if (user.name == album.artists[0].split(",")[0]) {
//                        albumUsers.add(user)
//                    }
//                }
//                val adapter = PlaylistAuthorAdapter(albumUsers)
//                playlistAuthorRecyclerView.adapter = adapter
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//    }

    private fun getAlbum(): Album? {
        return AlbumManager.getAlbum()
    }

    private fun setupAlbumDetails(album: Album?) {
        if (album != null) {
            playlistCover = view?.findViewById(R.id.playlistCover)!!
            playlistName = view?.findViewById(R.id.playlistName)!!
            playlistDescription = view?.findViewById(R.id.playlistDescription)!!

            Glide.with(this)
                .load(album.coverArtUrl)
                .into(playlistCover)
            playlistName.text = album.name
            playlistDescription.text = "Released: " + album.releaseDate
        }
    }


//    private fun setupSongsListForAlbum(playlist: Playlist?) {
//        val songsList = mutableListOf<Song>()
//        val songIds = playlist?.let { extractSongIdsFromAlbum(it) } ?: listOf()
//
//        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
//        playlistRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        fetchAllSongsFromFirebase { allSongs ->
//            val songsInPlaylist = allSongs.filter { it.id in songIds }
//            songsList.addAll(songsInPlaylist)
//            val newSongsList = createNewSongsList(songsList)
//            setupAdapter(newSongsList)
//        }
//    }

    private fun setupSongsListForAlbum(album: Album?) {
        val songIds = album?.songIds ?: listOf()
        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
        playlistRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        Log.d("AlbumDetailFragment", "Initialized setup list for album")

        getAllSongs { allSongs ->
            Log.d("AlbumDetailFragment", "song ids ${songIds.toString()}")

            Log.d("AlbumDetailFragment", "All Songs${allSongs.toString()}")

            val songsInPlaylist = allSongs.filter { it.id in songIds }
            Log.d("AlbumDetailFragment", "Song added${songsInPlaylist.toString()}")

            songsList.addAll(songsInPlaylist)
            playlistAdapter = AlbumAddSongAdapter(createNewSongsList(songsList), songsList)
            playlistRecyclerView.adapter = playlistAdapter
            playlistAdapter.notifyDataSetChanged()
        }
    }


    private fun createNewSongsList(songsList: MutableList<Song>): MutableList<SongNew> {
        val newSongsList = mutableListOf<SongNew>()
        for (song in songsList) {
            newSongsList.add(SongNew(song.coverArtUrl, song.songName, song.artist, song.id))
        }
        return newSongsList
    }


    private lateinit var backButton: ImageView
    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        playlistDescription = view?.findViewById(R.id.playlistDescription)!!
//        playlistAuthorRecyclerView = view?.findViewById(R.id.playlistAuthorsRecyclerView)!!
        playlistPlayButton = view?.findViewById(R.id.playImageView)!!


    }

    fun setUpOnClickListeners() {
        backButton.setOnClickListener {
            AlbumManager.clearAlbums()
            requireActivity().supportFragmentManager.popBackStack()
        }

        playlistPlayButton.setOnClickListener {
            Log.d("AlbumDetailFragment", "Play button clicked")
            SongManager.getInstance().addSongsFromPlaylistToQueue(songsList)
            MusicServiceManager.broadCastSongSelected(songsList[0])
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlbumDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlbumDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}