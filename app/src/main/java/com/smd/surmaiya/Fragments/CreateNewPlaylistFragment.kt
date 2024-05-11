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
import android.widget.Spinner
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private lateinit var playlistName: EditText
    private lateinit var playlistDescription: EditText
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
        initializeViews(view)
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

    fun initializeViews(view: View) {
        cancelButton = view.findViewById(R.id.cancelButton)!!
        playlistCover = view.findViewById(R.id.playlistCoverArt)!!
        playlistName = view.findViewById(R.id.playlistName)!!
        playlistDescription = view.findViewById(R.id.playlistDescription)!!
        privacySpinner = view.findViewById(R.id.privacySpinner)!!
        createButton = view.findViewById(R.id.createButton)!!

    }

    private fun buildPlaylistObject() {
        if (playlistName.text.toString().isEmpty()) {
            CustomToastMaker().showToast(requireContext(), "Please enter a playlist name")
            return
        }

        val playlistId = "id"
        val playlistName = this.playlistName.text.toString()
        val songIdsList = mutableListOf<String>()
        songIdsList.addAll(songIds)
        val userIdsList = mutableListOf<String>()
        userIdsList.add(UserManager.getCurrentUser()!!.id)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val dateAdded = mutableListOf<String>()
        dateAdded.add(currentDate)
        val followers = 0L
        val visibility = "public"
        val followerList = mutableListOf<String>()
        followerList.add("Nobody")
        getCoverArtUrlAfterUpload { coverArtDownload ->
            val playlist = Playlist(
                playlistId,
                playlistName,
                songIdsList,
                coverArtDownload,
                userIdsList,
                dateAdded,
                followers,
                followerList,
                visibility,
                playlistDescription.text.toString()
            )
            FirebaseDatabaseManager.uploadPlaylistToFirebase(playlist)

        }
    }

    private fun getCoverArtUrlAfterUpload(callback: (String) -> Unit) {
        if (coverArtUrl != null) {
            val pathString = "Playlist/${UserManager.getCurrentUser()!!.id}/${playlistName.text.toString()}/CoverArt/"
            FirebaseStorageManager.uploadToFirebaseStorage(coverArtUrl!!, pathString + getFileName(coverArtUrl!!)!!) { downloadUrl ->
                callback(downloadUrl)
            }
        } else {
             CustomToastMaker().showToast(requireContext(), "Please select a cover art")
        }
    }

    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        playlistCover.setOnClickListener {

            uploadPlaylistCover()
        }

        createButton.setOnClickListener {
            buildPlaylistObject()
            FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(LibraryFragment())
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