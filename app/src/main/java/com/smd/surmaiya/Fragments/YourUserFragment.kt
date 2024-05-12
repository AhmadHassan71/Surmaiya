package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.itemClasses.Playlist

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YourUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var followersCountTextView: TextView
    private lateinit var followingCountTextView: TextView
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
        val view = inflater.inflate(R.layout.fragment_your_user, container, false)
        return view
    }
    private fun intializeViews(view: View) {
        playlistRecyclerView = view.findViewById(R.id.PlaylistsRecylerView)
        followersCountTextView = view.findViewById(R.id.followersCount)
        followingCountTextView = view.findViewById(R.id.followingCount)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intializeViews(view)
        prepareClickListeners(view)
        prepareUserInformation(view)
        prepareYourPlaylists()
        applyUserInfo()
    }

    private fun prepareClickListeners(view: View) {
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val editProfileButton = view.findViewById<ImageView>(R.id.dotsButton)
        editProfileButton.setOnClickListener {
            val editProfileFragment = EditProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun prepareUserInformation(view: View) {
        val profilePic = view.findViewById<ImageView>(R.id.yourUserProfilePicture)
        Glide.with(requireActivity())
            .load(UserManager.getCurrentUser()?.profilePictureUrl)
            .into(profilePic)

        val username = view.findViewById<TextView>(R.id.username)
        username.text = UserManager.getCurrentUser()?.name
        Log.d("heh","User name is ${UserManager.getCurrentUser()?.name}")
        val email = view.findViewById<TextView>(R.id.userEmail)
        email.text = UserManager.getCurrentUser()?.email

    }
    private fun prepareYourPlaylists() {
        playlistRecyclerView.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.HORIZONTAL,false)


        FirebaseDatabaseManager.getPlaylists { playlists ->

            val yourPlaylists = playlists.filter { UserManager.getCurrentUser()?.id in it.userIds }

            val playlistAdapter = PlaylistAdapter(yourPlaylists, object : PlaylistAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
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
    private fun applyUserInfo() {
        followingCountTextView.text = UserManager.getCurrentUser()?.following?.size.toString()
        followersCountTextView.text = UserManager.getCurrentUser()?.followers?.size.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YourUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YourUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}