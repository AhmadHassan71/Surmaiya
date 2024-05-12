package com.smd.surmaiya.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SongOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongOptionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var song: Song? = null

    private lateinit var coverArt: ImageView
    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var likeTextView: TextView
    private lateinit var viewArtistTextView: TextView
    private lateinit var addToPlaylistTextView: TextView
    private lateinit var addToQueueTextView: TextView
    private lateinit var closeTextView: TextView
    private var isLiked = false
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
        return inflater.inflate(R.layout.fragment_song_options, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        song = arguments?.getParcelable("song", Song::class.java)

        initializeViews(view)
        setupOnClickListeners(view)
    }

    private fun getArtist() {
        val artists = song!!.artist.split(",")
        val firstArtist = artists[0]
        FirebaseDatabaseManager.getAllUsers { users ->
            for (user in users) {
                if (user.name == firstArtist) {
                    OtherUserManager.addUser(user)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ArtistPageFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun setupOnClickListeners(view: View) {
        viewArtistTextView.setOnClickListener {
            getArtist()
        }
        likeTextView.setOnClickListener {
            isLiked = !isLiked
            // Update the icon based on the state
            if (isLiked) {
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_filled, 0, 0, 0)
            } else {
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0)
            }

        }
        addToPlaylistTextView.setOnClickListener {
            FragmentHelper(requireActivity().supportFragmentManager, requireContext()).loadFragment(AddToPlaylistFragment())
        }
    }

    fun initializeViews(view: View) {
        coverArt = view.findViewById(R.id.coverArt)
        songNameTextView = view.findViewById(R.id.songNameTextView)
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        likeTextView = view.findViewById(R.id.likeTextView)
        viewArtistTextView = view.findViewById(R.id.viewArtistTextView)
        addToPlaylistTextView = view.findViewById(R.id.addToPlaylistTextView)
        addToQueueTextView = view.findViewById(R.id.addToQueueTextView)
        closeTextView = view.findViewById(R.id.closeTextView)

        song?.let {
            songNameTextView.text = it.songName
            artistNameTextView.text = it.artist
            Glide.with(this)
                .load(it.coverArtUrl)
                .into(coverArt)

        }

        closeTextView.setOnClickListener {
            activity?.onBackPressed()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SongOptionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SongOptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
