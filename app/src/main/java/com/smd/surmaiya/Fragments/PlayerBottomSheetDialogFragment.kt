package com.smd.surmaiya.Fragments

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smd.surmaiya.HelperClasses.ConnectedAudioDevice
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.SongManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song
import de.hdodenhof.circleimageview.CircleImageView

class PlayerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var song: Song
    private lateinit var progressBar: SeekBar
    private lateinit var pauseButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var circleImageView: CircleImageView
    private var nextButton : ImageView? = null
    private var previousButton : ImageView? = null
    private var dropDownButton : Button? = null
    private var threeDots:Button? = null
    private var closeArrow:ImageView? = null
    private var playerBarLinearLayout: LinearLayout? = null
    private var repeatButton: ImageView? = null

    private var lastClickTime: Long = 0

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                setupFullHeight(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.player_pop_up, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initializeViews(view)

        // Set up click listeners
        setUpOnClickListeners()

        // Load song details
        song = SongManager.getInstance().currentSong ?: return

        // Set song details
        songNameTextView.text = song.songName
        artistNameTextView.text = song.artist

        // Load song cover image and set palette colors
        loadSongCoverImage(song.coverArtUrl)

        // Start the image rotation animation
        startImageRotation()

        // Start updating the progress bar
        progressBar.post(updateProgressRunnable)
    }

    private fun initializeViews(view: View) {
        songNameTextView = view.findViewById(R.id.songNameTextView)
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        circleImageView = view.findViewById(R.id.albumCoverView)
        progressBar = view.findViewById(R.id.progressBar)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        nextButton = view.findViewById(R.id.nextButton)
        previousButton = view.findViewById(R.id.previousButton)
        dropDownButton = view.findViewById(R.id.dropdownButton)
        threeDots = view.findViewById(R.id.threedots)
        closeArrow = view.findViewById(R.id.closeButton)
        playerBarLinearLayout = view.findViewById(R.id.playerBarLinearLayout)
        repeatButton = view.findViewById(R.id.repeatButton)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setUpOnClickListeners() {

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

        repeatButton?.setOnClickListener {
            MusicServiceManager.isRepeatMode = !MusicServiceManager.isRepeatMode
            if (MusicServiceManager.isRepeatMode) {
                // If repeat mode is on, set the button background tint to green
                repeatButton?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.bottom_nav_primary))
            } else {
                // If repeat mode is off, set the button background tint to white
                repeatButton?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }


        dropDownButton?.setOnClickListener {
            dismiss() // This will close the bottom sheet when the dropdown button is clicked
        }

        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = MusicServiceManager.getService()?.exoPlayer?.duration ?: 0
                    val newProgress = (progress / 100.0) * duration
                    MusicServiceManager.getService()?.exoPlayer?.seekTo(newProgress.toLong())

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //pause runable
                progressBar.removeCallbacks(updateProgressRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                progressBar.post(updateProgressRunnable)
            }
        })
    }

    private fun loadSongCoverImage(coverArtUrl: String?) {
        Glide.with(requireContext())
            .asBitmap()
            .load(coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                @RequiresApi(Build.VERSION_CODES.S)
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    circleImageView.setImageBitmap(resource)
                    // Generate Palette from the Bitmap
                    Palette.from(resource).generate { palette ->
                        val dominantColor = palette?.dominantSwatch?.rgb ?: Color.parseColor("#EFE198")
                        // Set colors based on palette
                        setPaletteColors(dominantColor, palette)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle case where the Bitmap load is cleared
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setPaletteColors(dominantColor: Int, palette: Palette?) {


        // Set background colors
        palette?.let {
            val lightMutedColor = it.lightMutedSwatch?.rgb ?: Color.parseColor("#EFE198")
            val darkMutedColor = it.darkMutedSwatch?.rgb ?: Color.parseColor("#EFE198")
            val darkVibrantColor = it.darkVibrantSwatch?.rgb ?: Color.parseColor("#EFE198")
            val mutedColor = it.mutedSwatch?.rgb ?: Color.parseColor("#EFE198")
            val vibrantColor = it.vibrantSwatch?.rgb ?: Color.parseColor("#EFE198")
            val playingDeviceTextView: TextView = view?.findViewById(R.id.playingDeviceTextView) as TextView

            val colors = intArrayOf(lightMutedColor, mutedColor, darkMutedColor)


            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                colors
            )

            view?.let { view ->

                view.background = gradientDrawable
                playerBarLinearLayout?.setBackgroundColor(lightMutedColor)
                threeDots?.backgroundTintList = ColorStateList.valueOf(darkMutedColor)
                dropDownButton?.backgroundTintList = ColorStateList.valueOf(darkMutedColor)
                closeArrow?.backgroundTintList = ColorStateList.valueOf(lightMutedColor)
                val lightVibrantColor = it.lightVibrantSwatch?.rgb ?: Color.parseColor("#EFE198")
                val darkVibrantColor = it.darkVibrantSwatch?.rgb ?: Color.parseColor("#EFE198")

                // Existing code...

                // Create a LinearGradient with the light vibrant and dark vibrant colors
                val paint = songNameTextView.paint
                val width = paint.measureText(songNameTextView.text.toString())
                val textShader: Shader = LinearGradient(0f, 0f, width, songNameTextView.textSize,
                    intArrayOf(darkVibrantColor, mutedColor),
                    null,
                    Shader.TileMode.CLAMP)
                paint.shader = textShader
                songNameTextView.invalidate()
                artistNameTextView?.setTextColor(darkVibrantColor)
                playingDeviceTextView.setTextColor(darkVibrantColor)
                playerBarLinearLayout?.setBackgroundColor(lightMutedColor)
                threeDots?.backgroundTintList = ColorStateList.valueOf(darkMutedColor)


                // Calculate the complementary color
                val red = 255 - Color.red(darkVibrantColor)
                val green = 255 - Color.green(darkVibrantColor)
                val blue = 255 - Color.blue(darkVibrantColor)
                val complementaryColor = Color.rgb(red, green, blue)

                // Set the progress tint
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    progressBar.progressTintList = ColorStateList.valueOf(complementaryColor)

                }


                val connectedAudioDevice = ConnectedAudioDevice()
                playingDeviceTextView.text  = connectedAudioDevice.getConnectedAudioDevice(requireContext()).first
                // set the device start drawable
                playingDeviceTextView.setCompoundDrawablesWithIntrinsicBounds(connectedAudioDevice.getConnectedAudioDevice(requireContext()).second, 0, 0, 0)

                //change colour of this drawable
                playingDeviceTextView.compoundDrawableTintList = ColorStateList.valueOf(darkVibrantColor)


            }
        }
    }

    private fun startImageRotation() {
        val animator = ObjectAnimator.ofFloat(circleImageView, View.ROTATION, 0f, 360f)
        animator.duration = 20000 // Duration in milliseconds, adjust to suit your needs
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams

        var bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED

        //set minimimum height to parent height
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels
    }

    override fun onResume() {
        super.onResume()

       val intentFilter = IntentFilter().apply {
            addAction("com.smd.surmaiya.ACTION_PLAY")
            addAction("com.smd.surmaiya.ACTION_PAUSE")
           addAction("com.smd.surmaiya.ACTION_SONG_ENDED") // Add this line

       }
        requireContext().registerReceiver(playPauseReceiver, intentFilter)

        progressBar.post(updateProgressRunnable)
    }

    override fun onPause() {
        super.onPause()
        progressBar.removeCallbacks(updateProgressRunnable)
        requireContext().unregisterReceiver(playPauseReceiver)


    }

    private val playPauseReceiver = object : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
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
        }
    }
    }



    companion object {
        fun newInstance(song: Song): PlayerBottomSheetDialogFragment {
            val fragment = PlayerBottomSheetDialogFragment()
            val args = Bundle().apply {
                putParcelable("song", song)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            var progress = MusicServiceManager.getService()?.getProgress() ?: 0
            progress/=2
            progressBar.progress = progress
            progressBar.postDelayed(this, 2000)
        }
    }
}
