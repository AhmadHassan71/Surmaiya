package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArtistStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArtistStatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var albumsLabel: TextView
    private lateinit var albumCount: TextView
    private lateinit var songsCreatedLabel: TextView
    private lateinit var songsCreatedCount: TextView
    private lateinit var monthlyListenersLabel: TextView
    private lateinit var genreCount: TextView
    private lateinit var totalListenersCount: TextView
    private lateinit var artistName: TextView
    private lateinit var artistImage: ShapeableImageView
    private lateinit var genresLabel: TextView
    private var artist: User? = null


    private fun initializeViews(view: View) {
        albumsLabel = view.findViewById(R.id.albumsLabel)
        albumCount = view.findViewById(R.id.albumCount)
        songsCreatedLabel = view.findViewById(R.id.songsCreatedLabel)
        songsCreatedCount = view.findViewById(R.id.songsCreatedCount)
        monthlyListenersLabel = view.findViewById(R.id.monthlyListenersLabel)
        genreCount = view.findViewById(R.id.genreCount)
        totalListenersCount = view.findViewById(R.id.totalListenersCount)
        artistName = view.findViewById(R.id.artistName)
        artistImage = view.findViewById(R.id.artistImage)
        genresLabel = view.findViewById(R.id.genresLabel)
    }

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
        return inflater.inflate(R.layout.fragment_artist_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        prepareArtist()
        prepareArtistDetails()
    }

    private fun prepareArtist() {
        artist = OtherUserManager.getUser()
    }

    private fun prepareArtistDetails() {
        artistName.text = artist?.name
        totalListenersCount.text = artist?.followers?.size.toString()
        Glide.with(this).load(artist?.profilePictureUrl).into(artistImage)

        if (artist != null) {
            FirebaseDatabaseManager.getAllAlbums { albums ->
                var count = 0
                for (album in albums) {
                    if (album.artists.contains(artist!!.name)) {
                        count++
                    }
                }
                albumCount.text = count.toString()

            }

        }
        if (artist != null) {
            FirebaseDatabaseManager.getAllSongs { songs ->
                var count = 0
                var genres:Set<String> = setOf()
                for (song in songs) {
                    if (song.artist.contains(artist!!.name)) {
                        genres = genres.union(song.genres)
                        count++
                    }
                }
                genreCount.text = genres.size.toString()
                songsCreatedCount.text = count.toString()
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArtistStatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArtistStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}