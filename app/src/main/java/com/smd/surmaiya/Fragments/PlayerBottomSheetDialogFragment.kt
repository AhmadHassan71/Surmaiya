package com.smd.surmaiya.Fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song
import de.hdodenhof.circleimageview.CircleImageView
import android.animation.ObjectAnimator
import android.os.Build
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.smd.surmaiya.HelperClasses.ConnectedAudioDevice

class PlayerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme
    private var progressBar: ProgressBar? = null

    private var song: Song? = null
    private var pauseButton: ImageView? = null
    private var songNameTextView: TextView? = null
    private var artistNameTextView: TextView? = null
    private var circleImageView: CircleImageView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
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

        song = arguments?.getParcelable<Song>("song")

        songNameTextView = view.findViewById(R.id.songNameTextView)
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        circleImageView = view.findViewById(R.id.albumCoverView)

        songNameTextView?.text = song?.songName
        artistNameTextView?.text = song?.artist
        progressBar = view.findViewById(R.id.progressBar)

        setUpOnClickListeners()

        val playingDeviceTextView: TextView = view.findViewById(R.id.playingDeviceTextView)
        val connectedAudioDevice = ConnectedAudioDevice()
        playingDeviceTextView.text  = connectedAudioDevice.getConnectedAudioDevice(requireContext()).first
        // set the device start drawable
        playingDeviceTextView.setCompoundDrawablesWithIntrinsicBounds(connectedAudioDevice.getConnectedAudioDevice(requireContext()).second, 0, 0, 0)


        Glide.with(requireContext())
            .load(song?.coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(circleImageView!!)

        val dropdownButton = view.findViewById<Button>(R.id.dropdownButton)
        dropdownButton.setOnClickListener {
            dismiss() // This will close the bottom sheet when the dropdown button is clicked
        }

        var bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED

        //set minimimum height to parent height
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels



        startImageRotation() // Start the image rotation
        progressBar?.post(updateProgressRunnable) // Start updating the progress bar

    }

    fun setUpOnClickListeners() {

        // Implement play, pause, and stop functionality
        val playButton = requireView().findViewById<ImageView>(R.id.pauseButton)
        playButton.setOnClickListener {
            MusicServiceManager.pauseSong()
            playButton.visibility = View.GONE
            pauseButton?.visibility = View.VISIBLE

        }

        pauseButton = requireView().findViewById<ImageView>(R.id.playButton)

        pauseButton?.setOnClickListener {
            MusicServiceManager.resumeSong()
            pauseButton?.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {
        fun newInstance(song: Song): PlayerBottomSheetDialogFragment {
            val fragment = PlayerBottomSheetDialogFragment()
            val args = Bundle()
            args.putParcelable("song", song)
            fragment.arguments = args
            return fragment
        }
    }

    private fun startImageRotation() {
        val animator = ObjectAnimator.ofFloat(circleImageView, View.ROTATION, 0f, 360f)
        animator.duration = 10000 // Duration in milliseconds, adjust to suit your needs
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val progress = MusicServiceManager.getService()?.getProgress() ?: 0
            progressBar?.progress = progress
            progressBar?.postDelayed(this, 5)
        }
    }
}