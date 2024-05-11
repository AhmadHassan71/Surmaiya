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
import android.widget.Spinner
import android.widget.TextView
import com.bumptech.glide.Glide
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var playlistCover: ImageView
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
        return inflater.inflate(R.layout.fragment_edit_playlist, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()
        setupPrivacySpinner(view)
    }

    fun initializeViews() {
        cancelButton = view?.findViewById(R.id.cancelButton)!!
        saveButton = view?.findViewById(R.id.saveButton)!!

        playlistName = view?.findViewById(R.id.playlistName)!!
        playlistName.text = PlaylistManager.getPlaylists()?.playlistName

        playlistDescription = view?.findViewById(R.id.playlistDescription)!!
        playlistDescription.text = PlaylistManager.getPlaylists()?.playlistDescription

        playlistCover = view?.findViewById(R.id.playlistCoverArt)!!
        Glide.with(this).load(PlaylistManager.getPlaylists()?.coverArtUrl).into(playlistCover)

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

    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        saveButton.setOnClickListener {
            //save the changes
            saveChanges()
        }
        playlistCover.setOnClickListener {
            uploadPlaylistCover()
        }
    }

    private fun saveChanges() {
        val playlistNameView = view?.findViewById<EditText>(R.id.playlistName)
        val playlistDescriptionView = view?.findViewById<EditText>(R.id.playlistDescription)
        val privacySpinner = view?.findViewById<Spinner>(R.id.privacySpinner)

        val newPlaylistName = playlistNameView?.text.toString()
        val newPlaylistDescription = playlistDescriptionView?.text.toString()
        val newVisibility = privacySpinner?.selectedItem.toString()

        val playlist = PlaylistManager.getPlaylists()

        // Check if the playlist name has been changed
        if (newPlaylistName != playlist?.playlistName && newPlaylistName.isNotEmpty()) {
            playlist?.playlistName = newPlaylistName
        }

        // Check if the playlist description has been changed
        if (newPlaylistDescription != playlist?.playlistDescription && newPlaylistDescription.isNotEmpty()) {
            playlist?.playlistDescription = newPlaylistDescription
        }

        // Check if the visibility has been changed
        if (newVisibility != playlist?.visibility && newVisibility.isNotEmpty()) {
            playlist?.visibility = newVisibility
        }

        // Check if the cover art URL has been changed
        if (coverArtUrl != null && coverArtUrl.toString() != playlist?.coverArtUrl) {
            val pathString = "Playlist/${UserManager.getCurrentUser()!!.id}/${playlist?.playlistName}/CoverArt/"
            FirebaseStorageManager.uploadToFirebaseStorage(coverArtUrl!!, pathString + getFileName(coverArtUrl!!)!!) { downloadUrl ->
                playlist?.coverArtUrl = downloadUrl
                updatePlaylistInFirebase(playlist)
            }
        } else {
            updatePlaylistInFirebase(playlist)
        }

        FragmentHelper(requireActivity().supportFragmentManager,requireActivity()).loadFragment(PlaylistSearchFragment())
    }

    private fun updatePlaylistInFirebase(playlist: Playlist?) {
        if (playlist != null) {
            FirebaseDatabaseManager.updatePlaylistInFirebase(playlist) { success ->
                if (success) {
                    CustomToastMaker().showToast(requireContext(), "Playlist updated successfully")
                } else {
                    CustomToastMaker().showToast(requireContext(), "Error updating playlist")
                }
            }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditPlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val PICK_IMAGE_REQUEST = 1

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}