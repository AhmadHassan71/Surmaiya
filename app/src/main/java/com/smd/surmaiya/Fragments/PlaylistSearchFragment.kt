package com.smd.surmaiya.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager.fetchSongFromFirebase
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.UserManager
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
    private lateinit var playlistSearch: ImageView
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: AlbumAddSongAdapter
    private lateinit var playlistCover: ShapeableImageView
    private lateinit var playlistName: TextView
    private lateinit var followers: TextView
    private lateinit var followImage: ImageView
    private lateinit var playlistDescription: TextView
    private lateinit var addSong: ImageView
    private lateinit var addUserToPlaylist: ImageView
    private lateinit var editPlaylist: ImageView
    private lateinit var downloadPlaylist: ImageView
    private var isLiked: Boolean = false

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
            playlistDescription = view?.findViewById(R.id.playlistDescription)!!

            Glide.with(this)
                .load(playlist.coverArtUrl)
                .into(playlistCover)
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.playlistDescription
            "${playlist.followers} Followers".also { followers.text = it }
        }
    }

//    private fun setupSongsListForPlaylist(playlist: Playlist?) {
//        val songsList = mutableListOf<Song>()
//        val songIds = playlist?.let { extractSongIdsFromPlaylist(it) } ?: listOf()
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

    private fun setupSongsListForPlaylist(playlist: Playlist?) {
        val songsList = mutableListOf<Song>()
        val songIds = playlist?.let { extractSongIdsFromPlaylist(it) } ?: listOf()

        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
        playlistRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        for (songId in songIds) {
            if (songId != null) {
                fetchSongFromFirebase(songId) { song ->
                    song?.let {
                        songsList.add(it)
                        val newSongsList = createNewSongsList(songsList)
                        setupAdapter(newSongsList)
                    }
                }
            }
        }
    }
    private fun createNewSongsList(songsList: MutableList<Song>): MutableList<SongNew> {
        val newSongsList = mutableListOf<SongNew>()
        for (song in songsList) {
            newSongsList.add(SongNew(song.coverArtUrl, song.songName, song.artist, song.id))
        }
        return newSongsList
    }


    private fun setupAdapter(newSongsList: MutableList<SongNew>) {
        playlistAdapter = AlbumAddSongAdapter(newSongsList)
        playlistRecyclerView.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()
    }

    private fun extractSongIdsFromPlaylist(playlist: Playlist?): List<String> {
        val songIds = mutableListOf<String>()
        if (playlist != null) {
            songIds.addAll(playlist.songIds)
        }
        return songIds
    }

    private lateinit var backButton: ImageView
    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        editPlaylist = view?.findViewById(R.id.editPlaylist)!!
        playlistDescription = view?.findViewById(R.id.playlistDescription)!!
        followImage = view?.findViewById(R.id.likeImageView)!!
        addSong = view?.findViewById(R.id.addToPlaylist)!!
        addUserToPlaylist = view?.findViewById(R.id.addUserToPlaylist)!!
        downloadPlaylist = view?.findViewById(R.id.downloadPlaylist)!!

        if(getPlaylist()!!.followers > 0) {
            followImage.setImageResource(R.drawable.heart_filled)
        }

    }

    fun setUpOnClickListeners() {

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        editPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditPlaylistFragment()).addToBackStack(null).commit()
        }

        followImage.setOnClickListener {

            updatePlaylistFollower()
        }
        addSong.setOnClickListener {


            val bundle = Bundle()
            bundle.putString("selectedPlaylistId", PlaylistManager.getPlaylists()!!.playlsitId) // Replace with the actual ID of the selected playlist

            val addToPlaylistFragment = AddToPlaylistFragment()
            addToPlaylistFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addToPlaylistFragment)
                .addToBackStack(null)
                .commit()
            FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(AddToPlaylistFragment())
        }
        addUserToPlaylist.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddUserToPlaylistFragment()).addToBackStack(null).commit()
            showEnterEmailDialog(requireView())
        }
        downloadPlaylist.setOnClickListener {
//            downloadPlaylist.setImageResource(R.drawable.ic_baseline_cloud_download_24)

        }

    }

    private fun updatePlaylistFollower() {
        val playlist = getPlaylist()
        val userId =
            UserManager.getCurrentUser()?.id // Replace with the actual ID of the current user
        val followerIds = playlist?.followerIds?.toMutableList() ?: mutableListOf()
        val followersCount = playlist?.followers ?: 0

        if (userId in followerIds) {
            // The user is already a follower, so remove them from the followers list
            followerIds.remove(userId)
            playlist?.followers = followersCount - 1
            followImage.setImageResource(R.drawable.heart_empty)
        } else {
            // The user is not a follower, so add them to the followers list
            if (userId != null) {
                followerIds.add(userId)
            }
            playlist?.followers = followersCount + 1
            followImage.setImageResource(R.drawable.heart_filled)
        }

        playlist?.followerIds = followerIds

        // Update the playlist in Firebase
        FirebaseDatabaseManager.updatePlaylistInFirebase(playlist!!) { success ->
            if (success) {
                // Update the followers TextView
                "${playlist.followers} Followers".also { followers.text = it }
                isLiked = true
            } else {
                // Handle the error
            }
        }
    }

    private fun showEnterEmailDialog(view : View) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_collaborator, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
        val inviteButton = dialogView.findViewById<Button>(R.id.sendInviteButton)
        inviteButton.setOnClickListener {

        }
        dialog.show()
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