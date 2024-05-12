package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private var currentSong: Song? = null




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

        if(!MusicServiceManager.isPlaying())
            viewBackGround.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeOnClickListeners() {
        musicPlayer = findViewById(R.id.music_player)
        musicPlayer.setOnClickListener {

            if(currentSong!=null){
                val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
                val bundle = Bundle()
                bundle.putParcelable("song", currentSong)
                playerBottomSheetDialogFragment.arguments = bundle
                playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)
            }
        }

        playButton.setOnClickListener {
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            MusicServiceManager.playOrRestartSong()
        }

        pauseButton.setOnClickListener {
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            MusicServiceManager.pauseMusicAndBroadcast()
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



    }

    override fun onResume() {
            super.onResume()
            val intentFilter = IntentFilter().apply {
                addAction("com.smd.surmaiya.ACTION_PLAY")
                addAction("com.smd.surmaiya.ACTION_PAUSE")
                addAction("com.smd.surmaiya.ACTION_SONG_ENDED") // Add this line
                addAction("com.smd.surmaiya.ACTION_SONG_SELECTED")

            }
            registerReceiver(playPauseReceiver, intentFilter)
        }

        override fun onPause() {
            super.onPause()
            unregisterReceiver(playPauseReceiver)
        }


    private val playPauseReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            Log.d("HomeActivity", "onReceive: Action: $action")
            when (action) {
                "com.smd.surmaiya.ACTION_PLAY" -> {
                    playButton.visibility = View.GONE
                    pauseButton.visibility = View.VISIBLE
                }

                "com.smd.surmaiya.ACTION_PAUSE" -> {
                    pauseButton.visibility = View.GONE
                    playButton.visibility = View.VISIBLE
                }

                "com.smd.surmaiya.ACTION_SONG_ENDED" -> { // Add this case
                    pauseButton.visibility = View.GONE
                    playButton.visibility = View.VISIBLE
                }

                "com.smd.surmaiya.ACTION_SONG_SELECTED" -> {
                    val song = intent.getParcelableExtra<Song>("song")
                    Log.d("HomeActivity", "onReceive: Song selected: $song")
                    song?.let {

                        setSongOnPlayer(song)

//                        val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
//                        SongManager.getInstance().currentSong = song
//                        //if song is already playing we need to stop it and play the new song
//                        MusicServiceManager.playThisSongInstantly(song)
//                        val bundle = Bundle()
//                        bundle.putParcelable("song", song)
//                        playerBottomSheetDialogFragment.arguments = bundle
//                        playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)


                    }

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setSongOnPlayer(song: Song) {

        SongManager.getInstance().currentSong = song
        SongManager.getInstance().addToQueue(song)
        MusicServiceManager.playSong(song)
        songNameTextView.text = song.songName
        songNameTextView.requestFocus()
        viewBackGround.visibility = View.VISIBLE


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

        MusicServiceManager.playThisSongInstantly(song)

        currentSong = song


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