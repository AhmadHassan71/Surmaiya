package com.smd.surmaiya.adapters

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smd.surmaiya.Fragments.ArtistPageFragment
import com.smd.surmaiya.Fragments.PlaylistSearchFragment
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import jp.wasabeef.glide.transformations.BlurTransformation

class TopSongsAdapter(private val songs: List<Song>, private val isMonthlyRanking: Boolean, private val playlists: List<Playlist>) : RecyclerView.Adapter<TopSongsAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_monthly_ranking, parent, false)
        return SongViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]

        if (isMonthlyRanking) {
            // Load the song image and set the song and artist names for monthly ranking
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(currentSong.coverArtUrl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 1))) // Adjust these values as needed
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    @RequiresApi(Build.VERSION_CODES.S)
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        holder.songImageView.setImageBitmap(resource)
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
            holder.songNameTextView.text = currentSong.songName
            holder.artistNameTextView.text = currentSong.artist

            holder.songImageView.setOnClickListener {
                MusicServiceManager.broadCastSongSelected(currentSong)

            }


        } else {
            // Load the playlist image and set the playlist name and description for popular playlists
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(currentSong.coverArtUrl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 1))) // Adjust these values as needed
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    @RequiresApi(Build.VERSION_CODES.S)
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        holder.songImageView.setImageBitmap(resource)
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
            holder.songNameTextView.text = currentSong.songName
            holder.artistNameTextView.text = currentSong.artist

            // Set the playlist ID for each item
            // Use the playlistId as needed

            //if user clicks on image
            holder.songImageView.setOnClickListener {

                PlaylistManager.addPlaylist(playlists[position])

                val fragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PlaylistSearchFragment())
                    .addToBackStack(null)
                    .commit()

            }
        }
    }

    override fun getItemCount() = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImageView: ImageView = itemView.findViewById(R.id.songImageView)
        val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    }
}