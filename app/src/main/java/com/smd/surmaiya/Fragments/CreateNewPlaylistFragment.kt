package com.smd.surmaiya.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateNewPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class   CreateNewPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val songIds = mutableListOf<String>()
    private lateinit var createButton: Button
    private lateinit var cancelButton: Button
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var privacySpinner: Spinner
    private lateinit var playlistCover: ShapeableImageView
    private var coverArtUrl: Uri? = null

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
        return inflater.inflate(R.layout.fragment_create_new_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        receiveData()
        setUpOnClickListeners()
        setupPrivacySpinner(view)
    }

    private fun receiveData() {
        val bundle = this.arguments
        if (bundle != null) {
            songIds.addAll(bundle.getStringArrayList("selectedSongs") ?: emptyList())
        }
    }

    private fun setupPrivacySpinner(view:View) {
        val privacySpinner: Spinner = view.findViewById(R.id.privacySpinner)
        val privacyOptions = arrayOf("Public", "Private")
        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item, privacyOptions) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)

                view.isEnabled = position != 0 // Disable the first item
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.setTextColor(Color.WHITE) // Set the text color to white
                }
                return view
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item) // Set the drop-down view to spinner_item
        privacySpinner.adapter = adapter
    }

    fun initializeViews() {
        cancelButton = view?.findViewById(R.id.cancelButton)!!
        playlistCover = view?.findViewById(R.id.playlistCoverArt)!!
        playlistName = view?.findViewById(R.id.playlistName)!!
        playlistDescription = view?.findViewById(R.id.playlistDescription)!!
        privacySpinner = view?.findViewById(R.id.privacySpinner)!!
        createButton = view?.findViewById(R.id.createButton)!!

    }

    fun buildPlaylistObject(): Playlist {
        var coverArtDownload = ""

        coverArtDownload = getCoverArtUrlAfterUpload(coverArtDownload)

        if(playlistName.text.toString().isEmpty()) {
            CustomToastMaker().showToast(requireContext(), "Please enter a playlist name")
            return Playlist()
        }

        // Assuming you have these values available in your fragment
        val playlistId = "id"
        val playlistName = playlistName.text.toString()
        val mappedSongIds = mapOf("id" to songIds.joinToString(","))

        val userIds = mapOf("id" to "NxT2KMTSCLHqgBYpBCj")
        val dateAdded = "12/1/2024"
        val followers = 0L
        val visibility = "public"

        return Playlist(
            playlistId,
            playlistName,
            mappedSongIds,
            coverArtDownload,
            userIds,
            dateAdded,
            followers,
            visibility
        )
    }

    private fun getCoverArtUrlAfterUpload(coverArtDownload: String): String {
        var coverArtDownload1 = coverArtDownload
        val pathString: String =
            "Playlist/${UserManager.getCurrentUser()!!.id}/${playlistName}/CoverArt/"
        FirebaseStorageManager.uploadToFirebaseStorage(
            coverArtUrl!!,
            pathString + getFileName(coverArtUrl!!)!!
        ) {
            coverArtDownload1 = it
        }
        return coverArtDownload1
    }

    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        playlistCover.setOnClickListener {

            uploadPlaylistCover()
        }


    }
    private fun uploadPlaylistCover() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data!!
            // Use the downloadUrl here


//            val imageFileName = getFileName(filePath)

            val playlistName = view?.findViewById<EditText>(R.id.songName)
            if(playlistName?.text.toString().isEmpty()) {
                CustomToastMaker().showToast(requireContext(), "Please enter a playlist name")
                return
            }

            // Upload the image to Firebase Storage
//            coverArtUrl = uploadToFirebaseStorage(filePath, "Songs/${UserManager.getCurrentUser()!!.id}/Song/${songName?.text.toString()}/$imageFileName")
            coverArtUrl = filePath

            Glide.with(this).load(filePath).into(playlistCover)

        }
        
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateNewPlaylistFragment.
         */
        private const val PICK_IMAGE_REQUEST = 1
        private const val PICK_AUDIO_REQUEST = 2
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateNewPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}