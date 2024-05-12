package com.smd.surmaiya.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager.fetchSongFromFirebase
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.NotificationsManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.ManagerClasses.SongManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.adapters.PlaylistAuthorAdapter
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew
import com.smd.surmaiya.itemClasses.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaylistSearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var playlistSearch: ImageView
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: AlbumAddSongAdapter
    private lateinit var playlistCover: ShapeableImageView
    private lateinit var playlistName: TextView
    private lateinit var followers: TextView
    private lateinit var followImage: ImageView
    private lateinit var playlistDescription: TextView
    private lateinit var addSong: ImageView
    private lateinit var addUserToPlaylist: ImageView
    private lateinit var editPlaylist: ImageView
    private lateinit var downloadPlaylist: ImageView
    private var isLiked: Boolean = false
    private lateinit var playlistAuthorRecyclerView: RecyclerView
    private lateinit var playlistPlayButton: ImageView
    private var songsList = mutableListOf<Song>()
    private lateinit var playlistSearchView: SearchView
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


        return inflater.inflate(R.layout.fragment_playlist_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleCollaborationInvite()
        initializeViews()
        setUpOnClickListeners()
        setupRecyclerView()
        setUpSearchBar()
    }

    private fun handleCollaborationInvite() {
        if (requireActivity().intent.extras != null) {
            val playlistId = requireActivity().intent.extras?.getString("playlistId")
            val chatType = requireActivity().intent.extras?.getString("chat_type")
            val playlist = requireActivity().intent.getParcelableExtra<Playlist>("playlist")
            PlaylistManager.addPlaylist(playlist!!)
            showCollaborationDialogBox()
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().intent.removeExtra("playlistId")
        requireActivity().intent.removeExtra("chat_type")
        requireActivity().intent.removeExtra("playlist")
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().intent.removeExtra("playlistId")
        requireActivity().intent.removeExtra("chat_type")
        requireActivity().intent.removeExtra("playlist")
    }

    private fun showCollaborationDialogBox() {
        // show dialog to accept or reject the invitation
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_invitation, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
        val acceptButton = dialogView.findViewById<Button>(R.id.acceptButton)
        val rejectButton = dialogView.findViewById<Button>(R.id.rejectButton)
        val invitationText = dialogView.findViewById<TextView>(R.id.invitationText)
        invitationText.text = "You have been invited to collaborate on this Playlist"

        acceptButton.setOnClickListener {
            val userId = UserManager.getCurrentUser()?.id
            val userIds = PlaylistManager.getPlaylists()!!.userIds.toMutableList()
            if (userId != null) {
                userIds.add(userId)
            }
            PlaylistManager.getPlaylists()!!.userIds = userIds

            FirebaseDatabaseManager.updatePlaylistInFirebase(PlaylistManager.getPlaylists()!!) { success ->
                if (success) {
                    CustomToastMaker().showToast(
                        requireContext(),
                        "You have accepted the invitation"
                    )
                    dialog.dismiss()
                }
                // remove things from intent
                requireActivity().intent.removeExtra("playlistId")
                requireActivity().intent.removeExtra("chat_type")
                requireActivity().intent.removeExtra("playlist")
            }
        }
        rejectButton.setOnClickListener {
            dialog.dismiss()
            requireActivity().intent.removeExtra("playlistId")
            requireActivity().intent.removeExtra("chat_type")
            requireActivity().intent.removeExtra("playlist")
        }
        dialog.show()
    }

    private fun setupRecyclerView() {
        val playlist = getPlaylist()
        setupPlaylistDetails(playlist)
        setupSongsListForPlaylist(playlist)
        playlistAuthorRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        playlistAuthorRecyclerView.adapter = PlaylistAuthorAdapter(listOf())
        playlist?.userIds.let {
            if (it != null) {
                Log.d("PlaylistSearchFragment", "UserIds: $it")
                FirebaseDatabaseManager.getUsersByIds(it) { users ->
                    Log.d("PlaylistSearchFragment", "Users: $users")
                    val playlistAuthorAdapter = PlaylistAuthorAdapter(users)
                    playlistAuthorRecyclerView.adapter = playlistAuthorAdapter
                    playlistAuthorAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun getPlaylist(): Playlist? {
        return PlaylistManager.getPlaylists()
    }

    private fun setupPlaylistDetails(playlist: Playlist?) {
        if (playlist != null) {
            playlistCover = view?.findViewById(R.id.playlistCover)!!
            playlistName = view?.findViewById(R.id.playlistName)!!
            followers = view?.findViewById<TextView>(R.id.followers)!!
            playlistDescription = view?.findViewById(R.id.playlistDescription)!!

            Glide.with(this)
                .load(playlist.coverArtUrl)
                .into(playlistCover)
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.playlistDescription
            "${playlist.followers} Followers".also { followers.text = it }

        }
    }


//    private fun setupSongsListForPlaylist(playlist: Playlist?) {
//        val songsList = mutableListOf<Song>()
//        val songIds = playlist?.let { extractSongIdsFromPlaylist(it) } ?: listOf()
//
//        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
//        playlistRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        fetchAllSongsFromFirebase { allSongs ->
//            val songsInPlaylist = allSongs.filter { it.id in songIds }
//            songsList.addAll(songsInPlaylist)
//            val newSongsList = createNewSongsList(songsList)
//            setupAdapter(newSongsList)
//        }
//    }

    private fun setupSongsListForPlaylist(playlist: Playlist?) {

        val songIds = playlist?.let { extractSongIdsFromPlaylist(it) } ?: listOf()

        playlistRecyclerView = view?.findViewById(R.id.playlistRecyclerView)!!
        playlistRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        for (songId in songIds) {
            if (songId != null) {
                fetchSongFromFirebase(songId) { song ->
                    song?.let {
                        songsList.add(it)
                        val newSongsList = createNewSongsList(songsList)
                        setupAdapter(newSongsList)
                    }
                }
            }
        }
    }

    private fun createNewSongsList(songsList: MutableList<Song>): MutableList<SongNew> {
        val newSongsList = mutableListOf<SongNew>()
        for (song in songsList) {
            newSongsList.add(SongNew(song.coverArtUrl, song.songName, song.artist, song.id))
        }
        return newSongsList
    }


    private fun setupAdapter(newSongsList: MutableList<SongNew>) {
        playlistAdapter = AlbumAddSongAdapter(newSongsList, songsList)
        playlistRecyclerView.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()
    }


    private fun extractSongIdsFromPlaylist(playlist: Playlist?): List<String> {
        val songIds = mutableListOf<String>()
        if (playlist != null) {
            songIds.addAll(playlist.songIds)
        }
        return songIds
    }

    private lateinit var backButton: ImageView
    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        editPlaylist = view?.findViewById(R.id.editPlaylist)!!
        playlistDescription = view?.findViewById(R.id.playlistDescription)!!
        followImage = view?.findViewById(R.id.likeImageView)!!
        addSong = view?.findViewById(R.id.addToPlaylist)!!
        addUserToPlaylist = view?.findViewById(R.id.addUserToPlaylist)!!
        downloadPlaylist = view?.findViewById(R.id.downloadPlaylist)!!
        playlistAuthorRecyclerView = view?.findViewById(R.id.playlistAuthorsRecyclerView)!!
        playlistPlayButton = view?.findViewById(R.id.playImageView)!!
        playlistSearchView = view?.findViewById(R.id.searchPlaylist)!!

        if (getPlaylist()!!.followers > 0) {
            followImage.setImageResource(R.drawable.heart_filled)
        }

        if (UserManager.getCurrentUser()?.id !in PlaylistManager.getPlaylists()!!.userIds) {
            editPlaylist.visibility = View.GONE
            addUserToPlaylist.visibility = View.GONE
            addSong.visibility = View.GONE
        }



    }

    fun setUpSearchBar() {
        playlistSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    return false
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    val filteredList = songsList.filter { song ->
                        song.songName.contains(newText, ignoreCase = true)
                    }
                    setupAdapter(createNewSongsList(filteredList.toMutableList()))
                } else {
                    setupAdapter(createNewSongsList(songsList.toMutableList()))
                }
                return false
            }
        })

    }

    fun setUpOnClickListeners() {

        backButton.setOnClickListener {
            PlaylistManager.removePlaylist()
            requireActivity().supportFragmentManager.popBackStack()
        }
        editPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditPlaylistFragment()).addToBackStack(null)
                .commit()
        }

        followImage.setOnClickListener {

            updatePlaylistFollower()
        }
        addSong.setOnClickListener {


            val bundle = Bundle()
            bundle.putString(
                "selectedPlaylistId",
                PlaylistManager.getPlaylists()!!.playlsitId
            ) // Replace with the actual ID of the selected playlist

            val addToPlaylistFragment = AddToPlaylistFragment()
            addToPlaylistFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addToPlaylistFragment)
                .addToBackStack(null)
                .commit()
            FragmentHelper(requireActivity().supportFragmentManager, requireContext()).loadFragment(
                AddToPlaylistFragment()
            )
        }
        addUserToPlaylist.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddUserToPlaylistFragment()).addToBackStack(null).commit()
            showEnterEmailDialog(requireView())
        }
        downloadPlaylist.setOnClickListener {
//            downloadPlaylist.setImageResource(R.drawable.ic_baseline_cloud_download_24)

        }

        playlistPlayButton.setOnClickListener {
            Log.d("PlaylistSearchFragment", "Play button clicked")
            SongManager.getInstance().addSongsFromPlaylistToQueue(songsList)
            MusicServiceManager.broadCastSongSelected(songsList[0])
        }

    }

    private fun updatePlaylistFollower() {
        val playlist = getPlaylist()
        val userId =
            UserManager.getCurrentUser()?.id // Replace with the actual ID of the current user
        val followerIds = playlist?.followerIds?.toMutableList() ?: mutableListOf()
        val followersCount = playlist?.followers ?: 0

        if (userId in followerIds) {
            // The user is already a follower, so remove them from the followers list
            followerIds.remove(userId)
            playlist?.followers = followersCount - 1
            followImage.setImageResource(R.drawable.heart_empty)
        } else {
            // The user is not a follower, so add them to the followers list
            if (userId != null) {
                followerIds.add(userId)
            }
            playlist?.followers = followersCount + 1
            followImage.setImageResource(R.drawable.heart_filled)
        }

        playlist?.followerIds = followerIds

        // Update the playlist in Firebase
        FirebaseDatabaseManager.updatePlaylistInFirebase(playlist!!) { success ->
            if (success) {
                // Update the followers TextView
                "${playlist.followers} Followers".also { followers.text = it }
                isLiked = true
            } else {
                // Handle the error
            }
        }
    }

    private fun showEnterEmailDialog(view: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_collaborator, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
        val inviteButton = dialogView.findViewById<Button>(R.id.sendInviteButton)
        inviteButton.setOnClickListener {
            val collaboratorEmail =
                dialogView.findViewById<TextView>(R.id.emailEditText).text.toString()
            if (collaboratorEmail.isNotEmpty()) {
                sendInvitationNotification(collaboratorEmail)
            } else {
                CustomToastMaker().showToast(requireContext(), "Please enter an email address")
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun sendInvitationNotification(collaboratorEmail: String) {

        val title = "Invitation to Collaborate"
        val message =
            "${UserManager.getCurrentUser()!!.name} has invited you to collaborate on ${getPlaylist()!!.playlistName}"
        var colaborID: String? = null
        findCollaborator(collaboratorEmail) { collaborator ->

            colaborID = collaborator.id
            if (collaborator.fcmToken.isNotEmpty()) {

                NotificationsManager.sendCollaborationNotification(
                    title,
                    message,
                    collaborator.fcmToken,
                    PlaylistManager.getPlaylists()!!.playlsitId
                )

            } else {
                CustomToastMaker().showToast(requireContext(), "User not found")
            }
        }

        Handler().postDelayed({
            FirebaseDatabaseManager.addNotificationToUser(
                colaborID!!,
                title,
                message
            )
        }, 8000)


    }

    private fun findCollaborator(collaboratorEmail: String, callback: (User) -> Unit) {
        var collaborator: User

        FirebaseDatabaseManager.getAllUsers { users ->
            for (user in users) {
                Log.d("PlaylistSearchFragment", "User: $user")
                if (user.email == collaboratorEmail) {
                    collaborator = user
                    callback(collaborator)
                }
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
         * @return A new instance of fragment PlaylistSearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaylistSearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}