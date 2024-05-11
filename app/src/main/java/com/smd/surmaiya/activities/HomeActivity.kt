package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.HelperClasses.ConnectedAudioDevice
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.SongManager
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
    private lateinit var albumImageView: ImageView
    private lateinit var viewBackGround: LinearLayout
    private lateinit var progressBar: ProgressBar




    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MusicServiceManager.bindService(this)

        BottomNavigationHelper(this).loadFragment(HomeFragment())
        BottomNavigationHelper(this).setUpBottomNavigation()

        initalizeNotificationsChannel()
        initializeViews()

       initializeOnClickListeners()
    }

    private fun initalizeNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.music_channel_id)
            val name = getString(R.string.music_channel_name)
            val descriptionText = getString(R.string.music_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeViews() {
        songNameTextView = findViewById(R.id.songNameTextView)
        playingDeviceTextView = findViewById(R.id.playingDeviceTextView)
        deviceImage = findViewById(R.id.deviceImage)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        likeButton = findViewById(R.id.likeButton)
        // Initialize views
        albumImageView = findViewById(R.id.albumImageView)
        viewBackGround = findViewById(R.id.viewBackGround)
        progressBar = findViewById(R.id.progressBar)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeOnClickListeners() {
        musicPlayer = findViewById(R.id.music_player)
        musicPlayer.setOnClickListener {

            Log.d("HomeActivity", "initializeOnClickListeners: Music player clicked")
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
        SongManager.getInstance().currentSong = song
        SongManager.getInstance().addToQueue(song)
        MusicServiceManager.playSong(song)
        songNameTextView.text = song.songName
        songNameTextView.requestFocus()


        val connectedAudioDevice = ConnectedAudioDevice()
        playingDeviceTextView.text = connectedAudioDevice.getConnectedAudioDevice(this).first
        deviceImage.setImageResource(connectedAudioDevice.getConnectedAudioDevice(this).second)

        // Load album image
        Glide.with(this)
            .load(song.coverArtUrl)
            .into(albumImageView)

        var albumArtBitmap: Bitmap? = null

        Glide.with(this)
            .asBitmap()
            .load(song.coverArtUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        // Get the muted color from the palette
                        val mutedColor = palette?.mutedSwatch?.rgb ?: Color.parseColor("#550A1C")
                        albumArtBitmap = resource

                        Log.d("HomeActivity", "album art bitmap : $albumArtBitmap")

                        albumArtBitmap?.let {
                            Log.d("HomeActivity", "showPlayerBottomSheetDialog: Album art bitmap is not null")
                            MusicServiceManager.showNotification(song, it) }

                        // Set the background color of the viewBackGround
                        viewBackGround.setBackgroundColor(mutedColor)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle case where the Bitmap load is cleared
                }
            })



        progressBar.post(updateProgressRunnable)



        val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("song", song)
        playerBottomSheetDialogFragment.arguments = bundle
        playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)
    }




    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            var progress = MusicServiceManager.getService()?.getProgress()  ?: 0
            progressBar.progress = progress
            progressBar.postDelayed(this, 1000) // Update progress every second
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicServiceManager.unbindService(this)
    }
}