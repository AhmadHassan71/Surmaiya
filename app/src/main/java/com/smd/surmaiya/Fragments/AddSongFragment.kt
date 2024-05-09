package com.smd.surmaiya.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddSongFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddSongFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_add_song, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()

    }

    private lateinit var cancelButton: Button
    fun initializeViews() {
        cancelButton = view?.findViewById(R.id.cancelButton)!!

        val addArtworkLayout = view?.findViewById<LinearLayout>(R.id.addArtworkLayout)
        val addArtworkTextViewTitle = view?.findViewById<TextView>(R.id.addArtworkTextViewTitle)
        val addArtworkTextView = view?.findViewById<TextView>(R.id.addArtworkTextView)

        addArtworkTextView?.setOnClickListener {
            uploadSongCover()
        }
        addArtworkTextViewTitle?.setOnClickListener {
            uploadSongCover()
        }
        addArtworkLayout?.setOnClickListener {
            uploadSongCover()
        }



        val addSong = view?.findViewById<ImageView>(R.id.addSong)
        addSong?.setOnClickListener {
            uploadSong()
        }

        val createButton = view?.findViewById<Button>(R.id.createButton)
        createButton?.setOnClickListener {
            createSong()
        }

    }

    private fun createSong() {
        val songName = view?.findViewById<EditText>(R.id.songName)
        val songArtist = view?.findViewById<EditText>(R.id.songArtists)
//        val songGenre = view?.findViewById<EditText>(R.id.songGenre)
//        val songDuration = view?.findViewById<EditText>(R.id.songDuration)

        val songNameText = songName?.text.toString()
        val songArtistText = songArtist?.text.toString()
//        val songAlbumText = songAlbum?.text.toString()
//        val songGenreText = songGenre?.text.toString()
//        val songYearText = songYear?.text.toString()
//        val songDurationText = songDuration?.text.toString()
//        val songDescriptionText = songDescription?.text.toString()
//        val songLyricsText = songLyrics?.text.toString()

        if (songNameText.isEmpty() || songArtistText.isEmpty()) {
            return
        }

        // Create song

        val songToUpload = Song("id",songNameText,songArtistText,"album","duration","https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg","songUrl","releaseDate",0, listOf("genre"))

    }

    private fun uploadSong() {
        TODO("Not yet implemented")
    }

    private fun uploadSongCover() {


    }

    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val backButton = view?.findViewById<ImageView>(R.id.backButton)
        backButton?.setOnClickListener {
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
         * @return A new instance of fragment AddSongFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddSongFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}