package com.smd.surmaiya.Fragments

import BottomNavigationHelper
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.HelperClasses.FragmentNavigationHelper
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.HelperClasses.SideBarNavigationHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.activities.MonthlyRankingActivity
import com.smd.surmaiya.activities.PopularPlaylistsActivity
import com.smd.surmaiya.adapters.ListItemAdapter
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.adapters.RecentlyPlayedAdapter
import com.smd.surmaiya.adapters.TopGenresAdapter
import com.smd.surmaiya.itemClasses.ListItem
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.TopGenres


class HomeFragment : Fragment() {
    private lateinit var topGenresTextView: TextView
    private lateinit var topPlaylistTextView: TextView
    private lateinit var yourPlaylistTextView: TextView
    private lateinit var topAlbumsRecyclerView: RecyclerView
    private lateinit var topPlaylistsRecyclerView: RecyclerView
    private lateinit var yourPlaylistsRecyclerView: RecyclerView
    private lateinit var topGenresRecyclerView: RecyclerView
    private lateinit var recentlyPlayedRecyclerView: RecyclerView
    private lateinit var userName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalizeViews()



        prepareTopGenres()

        if(UserManager.getCurrentUser() !=null){
            prepareYourPlaylists()
            prepareRecentlyPlayed()
        }
        else{
            handleGuests(view)

        }

        prepareTopAlbums()

        prepareTopPlaylists()

        setUpOnClickListeners()

        SideBarNavigationHelper(requireActivity()).openDrawerOnMenuClick(view, requireActivity())
        SideBarNavigationHelper(requireActivity()).setupNavigationView(requireActivity().findViewById(R.id.drawer_layout))
        SideBarNavigationHelper(requireActivity()).prepareSideBar(requireActivity())
//         use with these names
//        val menuOpener = view.findViewById<ImageView>(R.id.menu_opener)
//        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)


    }

    private fun handleGuests(view: View) {
        yourPlaylistTextView.visibility = View.GONE
        yourPlaylistsRecyclerView.visibility = View.GONE
        recentlyPlayedRecyclerView.visibility = View.GONE
        view.findViewById<TextView>(R.id.recentlyPlayedTextView).visibility = View.GONE
        userName.visibility = View.GONE
        requireActivity().findViewById<TextView>(R.id.welcomeTextView).text = "Welcome Guest"
    }

    fun initalizeViews(){
        PlaylistManager.removePlaylist()
        topGenresTextView = view?.findViewById(R.id.topGenresTextView)!!
        topPlaylistTextView = view?.findViewById(R.id.topPlaylistsTextView)!!
        yourPlaylistTextView = view?.findViewById(R.id.yourPlaylistsTextView)!!
        topAlbumsRecyclerView = view?.findViewById(R.id.topAlbumsRecyclerView)!!
        topPlaylistsRecyclerView = view?.findViewById(R.id.topPlaylistsRecyclerView)!!
        yourPlaylistsRecyclerView = view?.findViewById(R.id.yourPlaylistsRecyclerView)!!
        topGenresRecyclerView = view?.findViewById(R.id.topGenresRecyclerView)!!
        recentlyPlayedRecyclerView = view?.findViewById(R.id.recentlyPlayedRecyclerView)!!
        userName = view?.findViewById(R.id.usernameTextView)!!
        userName.text = UserManager.getCurrentUser()?.userType.toString()
    }

    fun setUpOnClickListeners(){
        topGenresTextView.setOnClickListener {
            this.context?.let { it1 -> Navigator.navigateToActivity(it1, MonthlyRankingActivity::class.java) }
        }

        topPlaylistTextView.setOnClickListener {
            this.context?.let { it1 -> Navigator.navigateToActivity(it1, PopularPlaylistsActivity::class.java) }
        }

        yourPlaylistTextView.setOnClickListener {
            this.context?.let { it1 -> FragmentNavigationHelper(requireActivity()).loadFragment(AddAlbumFragment())}
        }

    }

    private fun prepareYourPlaylists() {
        yourPlaylistsRecyclerView.layoutManager = LinearLayoutManager(this.context,
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
            yourPlaylistsRecyclerView.adapter = playlistAdapter
        }
    }



    private fun prepareTopAlbums() {

        topAlbumsRecyclerView.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.HORIZONTAL,false)

        val listData = prepareListData() // Replace with your data loading logic



        val listItemAdapter = ListItemAdapter(listData)
        topAlbumsRecyclerView.adapter = listItemAdapter
    }

    private fun prepareTopPlaylists(){
        topPlaylistsRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)


        FirebaseDatabaseManager.getPlaylists { playlists ->

            val yourPlaylists = playlists.filter { "public" in it.visibility ||"Public" in it.visibility && UserManager.getCurrentUser()?.id !in it.userIds}


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
            topPlaylistsRecyclerView.adapter = playlistAdapter
        }

    }

    private fun prepareListData(): List<ListItem> {
        val listItems = mutableListOf<ListItem>()
        // Add your list items here
        listItems.add(ListItem(R.drawable.playlist, "Primary Text", "Secondary Text"))
        // ... add more list items
        return listItems
    }


    private fun prepareTopGenres() {

        val layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)
        topGenresRecyclerView.layoutManager = layoutManager

        val genreDataList = prepareGenreData()
        val genreAdapter = TopGenresAdapter(genreDataList)
        topGenresRecyclerView.adapter = genreAdapter
    }

    private fun prepareGenreData(): List<TopGenres> {
        val genres = mutableListOf<TopGenres>()
        genres.add(TopGenres(R.drawable.genre_image, "Rock"))
        genres.add(TopGenres(R.drawable.genre_image, "Pop"))
        genres.add(TopGenres(R.drawable.genre_image, "Metal"))
        genres.add(TopGenres(R.drawable.genre_image, "R&B"))

        return genres
    }

    private fun prepareRecentlyPlayed() {
        recentlyPlayedRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        val songList = prepareSongData()  // Replace with your data loading logic
        val songAdapter = RecentlyPlayedAdapter(songList)
        recentlyPlayedRecyclerView.adapter = songAdapter
    }

    private fun prepareSongData(): List<Song> {
        val songs = mutableListOf<Song>()
        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre"),"Album Name"))
        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre"),"Album Name"))
        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre"),"Album Name"))
        songs.add(Song("id", "Song Name", "Artist", "Album", "Duration", "https://upload.wikimedia.org/wikipedia/en/2/2a/2014ForestHillsDrive.jpg", "songUrl", "releaseDate", 0, listOf("genre"), "Album Name"))

        return songs
    }


}