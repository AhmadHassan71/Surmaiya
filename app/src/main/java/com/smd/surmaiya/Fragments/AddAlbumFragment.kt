package com.smd.surmaiya.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddAlbumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAlbumFragment : Fragment(), AddSongFragment.OnSongCreatedCallback  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var cancelButton: Button
    private lateinit var albumAddSongRecyclerView: RecyclerView
    private val songList = mutableListOf<SongNew>()
    private var songsToSendFirebase= mutableListOf<Song>()
    private lateinit var albumAddSongAdapter: AlbumAddSongAdapter

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_album, container, false)
    }


    private fun uploadSongs() {
        // Create the Album object
        val album = Album(
            id = UUID.randomUUID().toString(),
            name = "albumName",
            coverArtUrl = "",
            releaseDate = "releaseDate",
            songIds = songsToSendFirebase.map { it.id },
            null,
            artists = songsToSendFirebase.map { it.artist }
        )

        // Create a copy of the songsToSendFirebase list
        val songs = songsToSendFirebase.toList()

        // Upload the album cover art to Firebase Storage
        FirebaseStorageManager.uploadToFirebaseStorage(
            Uri.parse(album.coverArtUrl),
            "Albums/${UserManager.getCurrentUser()!!.id}/Albums/${album.id}/${album.coverArtUrl}.jpg"
        ) { url ->
            // Set the coverArtUrl of the album
            album.coverArtUrl = url

            // Counter to track the completion of each song upload
            var completedTasks = 0

            // Upload the songs to Firebase Storage
            for (song in songs) {
                FirebaseStorageManager.uploadToFirebaseStorage(
                    Uri.parse(song.songUrl),
                    "Albums/${UserManager.getCurrentUser()!!.id}/Albums/${album.id}/Songs/${song.songUrl}.mp3"
                ){ uri ->
                    // Set the coverArtUrl of the song
                    song.coverArtUrl = album.coverArtUrl
                    song.songUrl = uri.toString()

                    // Increment the counter
                    completedTasks++

                    // Check if all tasks are completed
                    if (completedTasks == songs.size) {
                        // Upload the album and songs to Firebase Database
                        FirebaseDatabaseManager.uploadAlbumAndSongs(album, songs)
                    }
                }
            }
        }
    }

    fun initializeViews() {

        cancelButton = view?.findViewById(R.id.cancelButton)!!
        albumAddSongRecyclerView = view?.findViewById(R.id.albumAddSongRecyclerView)!!
    }

    override fun onSongCreated(song: Song) {
        // Convert the Song object to SongNew object
        val songNew = SongNew(
            songName = song.songName,
            artistName = song.artist,
            songCoverImageResource = song.coverArtUrl
        )

        songsToSendFirebase.add(song)

        // Add the created song to the songList
        songList.add(songNew)

        // Notify the adapter that the data set has changed
        albumAddSongAdapter.notifyDataSetChanged()
    }

    fun setUpRecyclerView() {
        // Initialize the adapter with the songList
        albumAddSongAdapter = AlbumAddSongAdapter(songList)

        // Set the adapter for the RecyclerView
        albumAddSongRecyclerView.adapter = albumAddSongAdapter

        // Set the layout manager for the RecyclerView
        albumAddSongRecyclerView.layoutManager = LinearLayoutManager(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews() // Move this line here
        setUpOnClickListeners()
        setUpRecyclerView()
    }


    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddAlbumFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAlbumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}