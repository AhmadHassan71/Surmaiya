package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AddToPlaylistAdapter
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew

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
    private var selectedPlaylistId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPlaylistId = it.getString("selectedPlaylistId")
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
        for (song in songsList) {
            newSongsList.add(SongNew(song.coverArtUrl, song.songName, song.artist, song.id))
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

            val selectedPlaylist = PlaylistManager.getPlaylists()

            // Add the selected songs to the playlist
            if (selectedPlaylist != null) {
                val updatedSongsList = selectedPlaylist.songIds + selectedSongIds
                selectedPlaylist.songIds = updatedSongsList
                // Update the playlist in Firebase
                FirebaseDatabaseManager.updatePlaylistInFirebase(selectedPlaylist) { success ->
                    if (success) {
                        CustomToastMaker().showToast(requireContext(), "Songs Added Successfully")
                        FragmentHelper(requireActivity().supportFragmentManager, requireContext()).loadFragment(PlaylistSearchFragment())
                    } else {
                        CustomToastMaker().showToast(requireContext(), "Error adding songs to playlist")
                    }
                }
            }
            else {
                openCreateNewPlaylistFragment(selectedSongIds)
            }

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