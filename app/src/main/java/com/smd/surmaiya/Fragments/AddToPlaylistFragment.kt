package com.smd.surmaiya.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AddToPlaylistAdapter
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.adapters.AlbumSongAdapter
import com.smd.surmaiya.adapters.PlaylistSearchItemAdapter
import com.smd.surmaiya.itemClasses.PlaylistSearchItem
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
import kotlinx.coroutines.selects.select

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddToPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddToPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var backButton: ImageView
    private lateinit var createNewPlaylist: LinearLayout
    private lateinit var searchPlaylist: LinearLayout
    private lateinit var playlistSearchRecyclerView: RecyclerView
    private var songsList = mutableListOf<Song>()
    private var newSongsList = mutableListOf<SongNew>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()
        setUpRecyclerView()
    }

    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        createNewPlaylist = view?.findViewById(R.id.createNewPlaylist)!!
        searchPlaylist = view?.findViewById(R.id.searchPlaylist)!!
        playlistSearchRecyclerView = view?.findViewById(R.id.playlistSearchRecyclerView)!!
    }

    private fun setUpRecyclerView() {
        // Create dummy data for testing


        playlistSearchRecyclerView = view?.findViewById(R.id.playlistSearchRecyclerView)!!
        playlistSearchRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        FirebaseDatabaseManager.fetchAllSongsFromFirebase { songs ->
            songsList.addAll(songs)
            val newSongsList = createNewSongsList(songsList)
            val adapter = AddToPlaylistAdapter(newSongsList)
            playlistSearchRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
    private fun createNewSongsList(songsList: MutableList<Song>): MutableList<SongNew> {
        for (songs in songsList) {
            newSongsList.add(SongNew(songs.coverArtUrl,songs.songName,songs.artist))
        }
        return newSongsList
    }


    fun setUpOnClickListeners() {

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        createNewPlaylist.setOnClickListener {
            val selectedNewSongs: List<SongNew> = newSongsList.filter { it.isSelected }
            val selectedSongIds = mutableListOf<String>()

            getSelectedSongIds(selectedNewSongs, selectedSongIds)

            openCreateNewPlaylistFragment(selectedSongIds)


        }
        searchPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlaylistSearchFragment()).addToBackStack(null)
                .commit()
        }
    }

    private fun getSelectedSongIds(
        selectedNewSongs: List<SongNew>,
        selectedSongIds: MutableList<String>
    ) {
        for (newSong in selectedNewSongs) {
            val song = songsList.find { it.songName == newSong.songName }
            if (song != null) {
                selectedSongIds.add(song.id)
            }
        }
    }

    private fun openCreateNewPlaylistFragment(selectedSongIds: MutableList<String>) {
        val bundle = Bundle()
        bundle.putStringArrayList("selectedSongs", ArrayList(selectedSongIds))

        val nextFragment = CreateNewPlaylistFragment() // Replace 'NextFragment' with the actual class name of your next fragment
        nextFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddToPlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AddToPlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}