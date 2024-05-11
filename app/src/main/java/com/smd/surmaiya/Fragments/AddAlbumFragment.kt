package com.smd.surmaiya.Fragments

import android.net.Uri
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private lateinit var albumNameEditText: EditText
    private lateinit var albumArtworkImageView: ImageView
    private lateinit var layout_artwork: LinearLayout
    private var coverArtUrl: Uri? = null
    private lateinit var addSongButton: ImageView

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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadSongs() {
        // Create the Album object
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val releaseDate = LocalDate.now().format(formatter)

        val album = Album(
            id = UUID.randomUUID().toString(),
            name = albumNameEditText.text.toString(),
            coverArtUrl = coverArtUrl.toString(),
            releaseDate = releaseDate,
            songIds = mutableListOf(),
            null,
            artists = songsToSendFirebase.map { it.artist }
        )

        // Create a copy of the songsToSendFirebase list
        val songs = songsToSendFirebase.toList()

        // Upload the album cover art to Firebase Storage
        FirebaseStorageManager.uploadToFirebaseStorage(
            Uri.parse(album.coverArtUrl),
            "Albums/${UserManager.getCurrentUser()!!.id}/${album.id}/${album.id}.jpg"
        ) { url ->
            // Set the coverArtUrl of the album
            album.coverArtUrl = url

            // Counter to track the completion of each song upload
            var completedTasks = 0

            // Upload the songs to Firebase Storage
            for (song in songs) {
                FirebaseStorageManager.uploadToFirebaseStorage(
                    Uri.parse(song.songUrl),
                    "Albums/${UserManager.getCurrentUser()!!.id}/${album.id}/Songs/${song.id}.mp3"
                ){ uri ->
                    // Set the coverArtUrl of the song
                    song.coverArtUrl = album.coverArtUrl
                    song.album = album.id
                    song.songUrl = uri.toString()
                    album.songIds.add(song.id)
                    song.albumName= album.name
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
        albumNameEditText = view?.findViewById(R.id.edit_name)!!
        albumArtworkImageView = view?.findViewById(R.id.artworkImageView)!!
        layout_artwork = view?.findViewById(R.id.layout_artwork)!!
        addSongButton = view?.findViewById(R.id.image_plus)!!
    }

    override fun onSongCreated(song: Song) {
        // Convert the Song object to SongNew object
        val songNew = SongNew(
            songName = song.songName,
            artistName = song.artist,
            songCoverImageResource = song.coverArtUrl,
            songId = song.id
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



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews() // Move this line here
        setUpOnClickListeners()
        setUpRecyclerView()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        layout_artwork.setOnClickListener {
            // Open the gallery to select an image
            uploadAlbumCover()

        }
        val backButton = view?.findViewById<ImageView>(R.id.backButton)
        backButton?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val createButton = view?.findViewById<Button>(R.id.button_create)
        if (createButton != null) {
            createButton.setOnClickListener {
                uploadSongs()
            }
        }

        addSongButton.setOnClickListener {
            val addSongFragment = AddSongFragment.newInstance("", "", object : AddSongFragment.OnSongCreatedCallback {
                override fun onSongCreated(song: Song) {
                    // Convert the Song object to SongNew object
                    val songNew = SongNew(
                        songName = song.songName,
                        artistName = song.artist,
                        songCoverImageResource = song.coverArtUrl,
                        songId = song.id
                    )

                    songsToSendFirebase.add(song)

                    // Add the created song to the songList
                    songList.add(songNew)

                    // Notify the adapter that the data set has changed
                    albumAddSongAdapter.notifyDataSetChanged()

                    requireActivity().supportFragmentManager.popBackStack()

                }
            })
            val fragmentHelper = FragmentHelper(requireActivity().supportFragmentManager,requireContext())
            fragmentHelper.loadFragment(addSongFragment)
        }

    }


    private fun uploadAlbumCover() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            AddAlbumFragment.PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data!!
            // Use the downloadUrl here

            val artworkImageView = requireView().findViewById<ImageView>(R.id.artworkImageView)
            val imageFileName = getFileName(filePath)
            val songName = view?.findViewById<EditText>(R.id.edit_name)
            if (songName?.text.toString().isEmpty()) {
                CustomToastMaker().showToast(requireContext(), "Please enter a song name")
                return
            }

            // Upload the image to Firebase Storage
//            coverArtUrl = uploadToFirebaseStorage(filePath, "Songs/${UserManager.getCurrentUser()!!.id}/Song/${songName?.text.toString()}/$imageFileName")
            coverArtUrl = filePath
            if (artworkImageView != null) {
                Glide.with(this).load(filePath).into(artworkImageView)
            }
            // Set the visibility of the ImageView and LinearLayout
            Glide.with(this)
                .load(filePath)
                .listener(object : RequestListener<Drawable> {


                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false // Allow calling onResourceReady on the Target.
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("GLIDE", "Load failed", e)
                        // You can also log specific causes:
                        for (rootCause in e!!.rootCauses) {
                            Log.e("GLIDE", "Root cause", rootCause)
                        }
                        // Or, to log all details of the failure:
                        return false // Allow calling onLoadFailed on the Target.
                    }

                })
                .into(artworkImageView)
            artworkImageView?.visibility = View.VISIBLE
            val addArtworkLayout = view?.findViewById<LinearLayout>(R.id.layout_artwork)
            addArtworkLayout?.visibility = View.GONE

        }


    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1

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