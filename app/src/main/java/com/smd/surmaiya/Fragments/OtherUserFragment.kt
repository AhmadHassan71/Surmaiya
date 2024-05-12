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
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.User
import com.smd.surmaiya.itemClasses.UserType
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userProfileImage: CircleImageView
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var backButton: ImageView
    private lateinit var profileTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var followingCountTextView: TextView
    private lateinit var followersCountTextView: TextView
    private lateinit var followButton: Button
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun intializeViews(view: View) {
        userProfileImage = view.findViewById(R.id.otherUserProfilePicture)
        playlistRecyclerView = view.findViewById(R.id.publicPlaylistsRecyclerView)
        backButton = view.findViewById(R.id.backButton)
        profileTextView = view.findViewById(R.id.username)
        userEmailTextView = view.findViewById(R.id.userEmail)
        followingCountTextView = view.findViewById(R.id.followingCount)
        followersCountTextView = view.findViewById(R.id.followersCount)
        followButton = view.findViewById(R.id.follow_button)
    }

    private fun setUpRecyclerView() {
        val playlists = listOf<Playlist>()
        playlistAdapter = PlaylistAdapter(playlists, object : PlaylistAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle item click
            }
            override fun onItemChanged(position: Int) {
                // Handle item changed
            }
        })
//        FirebaseDatabaseManager.getPlaylistsByUserId()
        playlistRecyclerView.layoutManager = LinearLayoutManager(context)
        playlistRecyclerView.adapter = playlistAdapter
    }

    private fun setOnClickListeners() {
        backButton.setOnClickListener {
            fragmentManager?.popBackStack()

        }
        followButton.setOnClickListener {
            if (UserManager.getCurrentUser()?.userType != UserType.GUEST) {
                if (user!!.followers.contains(UserManager.getCurrentUser()?.id)) {
                    FirebaseDatabaseManager.unfollowUser(
                        UserManager.getCurrentUser()!!,
                        user!!
                    ) { success ->
                        if (success) {
                            followButton.text = "Follow"
                            followButton.text = "Following"
                            followersCountTextView.text = (user!!.followers.size - 1).toString()
                        } else {
                            Toast.makeText(context, "Failed to unfollow user", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    FirebaseDatabaseManager.followUser(
                        UserManager.getCurrentUser()!!,
                        user!!
                    ) { success ->
                        if (success) {
                            // Update the UI to reflect the new follow status
                            followButton.text = "Following"
                            followersCountTextView.text = (user!!.followers.size + 1).toString()
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
    }
    private fun prepareUser() {
        user = OtherUserManager.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_other_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intializeViews(view)
        setUpRecyclerView()
        prepareUser()
        applyUserInfo()
        setOnClickListeners()
        prepareYourPlaylists()
    }

    private fun applyUserInfo() {
        profileTextView.text = user?.name
        Glide.with(this).load(user?.profilePictureUrl).into(userProfileImage)
        userEmailTextView.text = user?.email
        followingCountTextView.text = user?.following?.size.toString()
        followersCountTextView.text = user?.followers?.size.toString()
        if (user!!.followers.contains(UserManager.getCurrentUser()?.id)) {
            followButton.text = "Following"
        } else {
            followButton.text = "Follow"
        }

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OtherUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtherUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}