package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.ManagerClasses.AlbumManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAdapter
import com.smd.surmaiya.adapters.SearchItemAdapter
import com.smd.surmaiya.interfaces.OnArtistClickListener
import com.smd.surmaiya.itemClasses.Album
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.User
import com.smd.surmaiya.itemClasses.UserType

class ArtistPageFragment : Fragment() {
    private lateinit var artistNameTextView: TextView
    private lateinit var albumCountTextView: TextView
    private lateinit var trackCountTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var songsTextView: TextView
    private lateinit var artistImageView: ImageView
    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var songAdapter: SearchItemAdapter
    private lateinit var songList: MutableList<Song>
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var albumList: MutableList<Album>
    private lateinit var follow_button: Button
    private var artist: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_page, container, false)
    }

    private fun initializeViews(view: View) {
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        albumCountTextView = view.findViewById(R.id.AlbumCountTextView)
        trackCountTextView = view.findViewById(R.id.TrackCountTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        albumTextView = view.findViewById(R.id.albumTextView)
        songsTextView = view.findViewById(R.id.songsTextView)
        artistImageView = view.findViewById(R.id.artistImage)
        albumRecyclerView = view.findViewById(R.id.albumsRecyclerView)
        songsRecyclerView = view.findViewById(R.id.songsRecyclerView)
        follow_button = view.findViewById(R.id.follow_button)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setOnClickListeners()
        prepareArtist()
        setUpSongsRecyclerView()
        setArtistDetails()
    }

    private fun setOnClickListeners() {
        follow_button.setOnClickListener {
            if (UserManager.getCurrentUser()?.userType != UserType.GUEST) {
                if (artist!!.followers.contains(UserManager.getCurrentUser()?.id)) {
                    FirebaseDatabaseManager.unfollowUser(
                        UserManager.getCurrentUser()!!,
                        artist!!
                    ) { success ->
                        if (success) {
                            follow_button.text = "Follow"
                            follow_button.text = "Following"
                        } else {
                            Toast.makeText(context, "Failed to unfollow user", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    FirebaseDatabaseManager.followUser(
                        UserManager.getCurrentUser()!!,
                        artist!!
                    ) { success ->
                        if (success) {
                            // Update the UI to reflect the new follow status
                            follow_button.text = "Following"
                        } else {
                            // Show an error message
                            Toast.makeText(context, "Failed to follow user", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please login to follow user", Toast.LENGTH_SHORT).show()
            }
        }
        artistImageView.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ArtistStatsFragment())
                .addToBackStack(null)
                .commit()

        }

    }

    private fun prepareArtist() {
        artist = OtherUserManager.getUser()
    }


    private fun setUpSongsRecyclerView() {
        songList = mutableListOf()
        songAdapter = SearchItemAdapter(mutableListOf(), object : OnArtistClickListener {
            override fun onArtistClick(artistName: String) {
                FirebaseDatabaseManager.getAllUsers { users ->
                    for (user in users) {
                        if (user.name == artistName) {
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
        },requireActivity().supportFragmentManager)
        songsRecyclerView.adapter = songAdapter
        songsRecyclerView.layoutManager = LinearLayoutManager(context)
        albumAdapter = AlbumAdapter(mutableListOf(), object : AlbumAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
            }

            override fun onItemChanged(position: Int) {
                // Do nothing
            }
        })
        albumRecyclerView.adapter = albumAdapter
        albumRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    private fun setArtistDetails() {
        artistNameTextView.text = artist?.name
        descriptionTextView.text = artist?.email
        albumTextView.text = "Albums"
        songsTextView.text = "Songs"
        Glide.with(this).load(artist?.profilePictureUrl).into(artistImageView)
        if (artist != null) {
            if (artist!!.followers.contains(UserManager.getCurrentUser()?.id)) {
                follow_button.text = "Following"
            } else {
                follow_button.text = "Follow"
            }
        }

        if (artist != null) {
            FirebaseDatabaseManager.getAllAlbums { albums ->
                albumList = mutableListOf()
                var count = 0
                for (album in albums) {
                    if (album.artists[0].split(",")[0]==(artist!!.name)) {
                        albumList.add(album)
                        count++
                    }
                }
                albumCountTextView.text = count.toString() + " Albums"

                albumAdapter = AlbumAdapter(albumList, object : AlbumAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        AlbumManager.addAlbum(albumList[position])
                        val fragmentManager = requireActivity().supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, AlbumDetailFragment())
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onItemChanged(position: Int) {
                    }
                })
                albumAdapter.notifyDataSetChanged()
                albumRecyclerView.adapter = albumAdapter
            }

        }
        if (artist != null) {
            songList.clear()
            FirebaseDatabaseManager.getAllSongs { songs ->
                var count = 0
                for (song in songs) {
                    if (song.artist.contains(artist!!.name)) {
                        songList.add(song)
                        count++
                    }
                }

                trackCountTextView.text = count.toString() + " Tracks"
                songAdapter = SearchItemAdapter(songList, object : OnArtistClickListener {
                    override fun onArtistClick(artistName: String) {
                        FirebaseDatabaseManager.getAllUsers { users ->
                            for (user in users) {
                                if (user.name == artistName) {
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
                },requireActivity().supportFragmentManager)
                songAdapter.notifyDataSetChanged()
                songsRecyclerView.adapter = songAdapter
            }

        }

    }


    companion object {
        @JvmStatic
        fun newInstance() = ArtistPageFragment()
    }
}