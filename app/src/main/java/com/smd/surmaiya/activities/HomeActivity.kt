package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.HelperClasses.ConnectedAudioDevice
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song

class HomeActivity : AppCompatActivity() {

    private lateinit var songNameTextView: TextView
    private lateinit var playingDeviceTextView: TextView
    private lateinit var deviceImage: ImageView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var musicPlayer: View


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MusicServiceManager.bindService(this)

        BottomNavigationHelper(this).loadFragment(HomeFragment())
        BottomNavigationHelper(this).setUpBottomNavigation()

        initializeViews()

       initializeOnClickListeners()
    }

    private fun initializeViews() {
        songNameTextView = findViewById(R.id.songNameTextView)
        playingDeviceTextView = findViewById(R.id.playingDeviceTextView)
        deviceImage = findViewById(R.id.deviceImage)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        likeButton = findViewById(R.id.likeButton)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeOnClickListeners() {
        musicPlayer = findViewById(R.id.music_player)
        musicPlayer.setOnClickListener {
            val songUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/surmaiya.appspot.com/o/Albums%2F-NxT2KMTSCLHqgBYpBCj%2F5a9cc003-ad95-4e95-b157-1cdc94946806%2FSongs%2F06ff2cb6-3f1f-403c-ac03-b0e5cd98bb9d.mp3?alt=media&token=ec0729e0-66a9-4fa9-b0c4-64103ec619fa")
            MusicServiceManager.playSong(songUri)
            showPlayerBottomSheetDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showPlayerBottomSheetDialog() {
        val song = Song(
            id = "songId",
            songName = "Dawn Fm",
            artist = "The Weeknd",
            album = "Dawn",
            duration = "songDuration",
            coverArtUrl = "https://firebasestorage.googleapis.com/v0/b/surmaiya.appspot.com/o/images%2F7ab62641-54ad-44a1-b7e3-77274463ce93?alt=media&token=186691b8-4444-4521-bfe4-2c851157e934",
            songUrl = "https://firebasestorage.googleapis.com/v0/b/surmaiya.appspot.com/o/Albums%2F-NxSBbl5FJxYcIbgjYzK%2Fd46f24fd-5e32-495a-b21f-958f79a1e55d%2FSongs%2F5f9289cf-bcee-4fd7-8194-41b92a3bcdca.mp3?alt=media&token=17723146-5bd6-465e-9dd6-6d0456160416",
            releaseDate = "releaseDate",
            numListeners = 0,
            genres = listOf("genre1", "genre2"),
            albumName = "albumName"
        )

        songNameTextView.text = song.songName

        val connectedAudioDevice = ConnectedAudioDevice()
        playingDeviceTextView.text = connectedAudioDevice.getConnectedAudioDevice(this).first
        deviceImage.setImageResource(connectedAudioDevice.getConnectedAudioDevice(this).second)


        val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("song", song)
        playerBottomSheetDialogFragment.arguments = bundle
        playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)
    }


    override fun onDestroy() {
        super.onDestroy()
        MusicServiceManager.unbindService(this)
    }
}