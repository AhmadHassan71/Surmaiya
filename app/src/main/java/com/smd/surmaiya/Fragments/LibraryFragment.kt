package com.smd.surmaiya.Fragments

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.HelperClasses.SideBarNavigationHelper
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.adapters.SearchItemAdapter
import com.smd.surmaiya.interfaces.OnArtistClickListener
import com.smd.surmaiya.itemClasses.Song

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private lateinit var backButton: ImageView
    private lateinit var addToPlaylist: ImageView
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var librarySongRecyclerView: RecyclerView
    private lateinit var librarySongAdapter: SearchItemAdapter
    private var songList: MutableList<Song> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setOnClickListeners()
        setUpLikedSongsRecyclerView()
        prepareYourPlaylists()
        SideBarNavigationHelper(requireActivity()).openDrawerOnMenuClick(view, requireActivity())
        SideBarNavigationHelper(requireActivity()).setupNavigationView(
            requireActivity().findViewById(
                R.id.drawer_layout
            )
        )
        getProfilePicture(view)

        SideBarNavigationHelper(requireActivity()).openDrawerOnMenuClick(view, requireActivity())

    }

    private fun getProfilePicture(view: View) {
        val pfp = view.findViewById<ImageView>(R.id.userProfilePicture1)
        Glide.with(this).load(UserManager.getCurrentUser()?.profilePictureUrl).into(pfp)
    }

    fun initializeViews() {
//        backButton = view?.findViewById(R.id.backButton)!!

        PlaylistManager.removePlaylist()
        addToPlaylist = view?.findViewById(R.id.addToPlaylist)!!
        playlistRecyclerView = view?.findViewById(R.id.libraryPlaylistsRecyclerView)!!

    }

    private fun setUpLikedSongsRecyclerView() {
        // Initialize songList and RecyclerView for liked songs
        songList = mutableListOf<Song>()
        librarySongRecyclerView = view?.findViewById(R.id.librarySongRecyclerView)!!

        // Fetch liked songs from Firebase and update songList
        FirebaseDatabaseManager.getLikedSongsFromFirebase() { songs ->
            FirebaseDatabaseManager.getAllSongs {
                for (song in songs) {
                    for (s in it) {
                        if (song == s.id) {
                            songList.add(s)
                        }
                    }
                }
                librarySongAdapter.notifyDataSetChanged()
            }
        }

        // Set up RecyclerView adapter and layout manager
        librarySongAdapter = SearchItemAdapter(songList, object : OnArtistClickListener {
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
        })
        },requireActivity().supportFragmentManager)


        librarySongRecyclerView.adapter = librarySongAdapter
        librarySongRecyclerView.layoutManager = LinearLayoutManager(context)

//        // Set up RecyclerView for filter items
//        val filterItems = mutableListOf(
//            "Item 1",
//            "Item 2",
//            "Item 3"
//        )
//        filterRecyclerView.adapter = LibraryFilterAdapter(filterItems)
//        filterRecyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun prepareYourPlaylists() {
        playlistRecyclerView.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.HORIZONTAL,false)

        FirebaseDatabaseManager.getPlaylists { playlists ->

            val yourPlaylists = playlists.filter { UserManager.getCurrentUser()?.id in it.userIds }


            val playlistAdapter = PlaylistAdapter(yourPlaylists, object : PlaylistAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click
                    // pass the playlist object to the next fragment
                    val playlist = yourPlaylists[position]
                    PlaylistManager.addPlaylist(playlist)
                    FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(PlaylistSearchFragment())
                }
                override fun onItemChanged(position: Int) {
                    // Handle item change
                }
            })
            playlistRecyclerView.adapter = playlistAdapter
        }
    }

    private fun setOnClickListeners() {
//        backButton.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }

        addToPlaylist.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddToPlaylistFragment()).addToBackStack(null)
                .commit()

        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LibraryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}