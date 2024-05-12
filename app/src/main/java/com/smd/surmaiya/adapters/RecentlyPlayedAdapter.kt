package com.smd.surmaiya.adapters

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song
import jp.wasabeef.glide.transformations.BlurTransformation

class RecentlyPlayedAdapter(private val songs: List<Song>) : RecyclerView.Adapter<RecentlyPlayedAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recently_played, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]


        // Set the song and artist names
        holder.songNameTextView.text = currentSong.songName
        holder.artistNameTextView.text = currentSong.artist

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(currentSong.coverArtUrl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 1))) // Adjust these values as needed
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                @RequiresApi(Build.VERSION_CODES.S)
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.songCoverImageView.setImageBitmap(resource)
                    // Generate Palette from the Bitmap
                    Palette.from(resource).generate { palette ->
                        val dominant = palette?.lightVibrantSwatch?.rgb ?: Color.parseColor("#FFFFFF")

                        //get opposite colour

                        val complementaryColour= Color.rgb(255 - Color.red(dominant), 255 - Color.green(dominant), 255 - Color.blue(dominant))

                        //get compl

                        //light vibrant no
                        //vibrant no
                        //complementary
                        //muted no
                        //lightvibrant yes
                        //light muted
                        //darkmuted



                        holder.songNameTextView.setTextColor(dominant)
                        holder.artistNameTextView.setTextColor(dominant)
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle case where the Bitmap load is cleared
                }
            })

        // Create a zoom in animation
        val zoomInX = android.animation.ObjectAnimator.ofFloat(holder.songCoverImageView, View.SCALE_X, 1.2f)
        val zoomInY = android.animation.ObjectAnimator.ofFloat(holder.songCoverImageView, View.SCALE_Y, 1.2f)

        // Create a zoom out animation
        val zoomOutX = android.animation.ObjectAnimator.ofFloat(holder.songCoverImageView, View.SCALE_X, 1.2f)
        val zoomOutY = android.animation.ObjectAnimator.ofFloat(holder.songCoverImageView, View.SCALE_Y, 1.2f)

        // Set the duration of the animations
        zoomInX.duration = 6000
        zoomInY.duration = 6000
        zoomOutX.duration = 6000
        zoomOutY.duration = 6000

        // Set a delay before the zoom out animation starts
        zoomOutX.startDelay = 1000
        zoomOutY.startDelay = 1000

        //infite loop

        zoomOutX.repeatCount = ObjectAnimator.INFINITE
        zoomOutY.repeatCount = ObjectAnimator.INFINITE

        zoomOutX.repeatMode = ObjectAnimator.REVERSE
        zoomOutY.repeatMode = ObjectAnimator.REVERSE





        // Start the animations
        zoomInX.start()
        zoomInY.start()
        zoomOutX.start()
        zoomOutY.start()
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCoverImageView: ImageView = itemView.findViewById(R.id.songCoverImageView)
        val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    }
}
