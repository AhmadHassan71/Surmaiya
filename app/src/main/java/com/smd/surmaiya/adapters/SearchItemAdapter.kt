package com.smd.surmaiya.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.Fragments.ArtistPageFragment
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.interfaces.OnArtistClickListener
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew

class SearchItemAdapter(
    private val searchItems: MutableList<Song>,
    private val onArtistClickListener: OnArtistClickListener,
    private val fragmentManager: FragmentManager // Add this line

) :
    RecyclerView.Adapter<SearchItemAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songNameTextView = holder.linearLayout.findViewById<TextView>(R.id.albumTextView1)
        val songArtistNameTextView = holder.linearLayout.findViewById<TextView>(R.id.albumTextView2)
        val songImageView = holder.linearLayout.findViewById<ImageView>(R.id.albumImageView)
        songNameTextView.text = searchItems[position].songName
        songArtistNameTextView.text = searchItems[position].artist
        if (searchItems[position].coverArtUrl != null) {
            Glide.with(holder.linearLayout.context)
                .load(searchItems[position].coverArtUrl).into(songImageView)
        }

        songArtistNameTextView.setOnClickListener {
            val artists = searchItems[position].artist.split(",")
            val firstArtist = artists[0]
            onArtistClickListener.onArtistClick(firstArtist)
        }

        holder.itemView.setBackgroundColor(Color.parseColor("#F3CC00"))
        holder.itemView.elevation = 0f
        val optionsImageView = holder.itemView.findViewById<ImageView>(R.id.optionsImageView)
        optionsImageView.setOnClickListener {
//            deleteOption(optionsImageView, holder)

            showOptions(optionsImageView, holder)
        }
        val likedImageView = holder.itemView.findViewById<ImageView>(R.id.likeImageView)
        FirebaseDatabaseManager.getLikedSongsFromFirebase { likedSongs ->
            // Check if the current song is in the list of liked songs
            if (searchItems[position].id in likedSongs) {
                likedImageView.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.heart_filled))
                searchItems[position].isLiked = true
            } else {
                likedImageView.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.heart_empty))
                searchItems[position].isLiked = false
            }
        }

        likedImageView.setOnClickListener {
            likeOption(likedImageView, holder)
        }

        songArtistNameTextView.setOnClickListener {
            val artists = searchItems[position].artist.split(",")
            Log.d("ArtistName", artists[0])
            val firstArtist = artists[0]

            FirebaseDatabaseManager.getAllUsers { users ->
                for (user in users) {
                    if (user.name == firstArtist) {
                        OtherUserManager.addUser(user)
                        val fragmentManager =
                            (holder.linearLayout.context as AppCompatActivity).supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ArtistPageFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

        }
        songImageView.setOnClickListener {
            val song = searchItems[position]
            MusicServiceManager.broadCastSongSelected(song)


        }



    }

    private fun showOptions(optionsImageView: ImageView?, holder: SearchItemAdapter.MyViewHolder) {
        Log.d("Options", "Options Clicked")

    }

    private fun likeOption(likedImageView: ImageView?, holder: SearchItemAdapter.MyViewHolder) {
        likedImageView?.setOnClickListener {
            val position = holder.adapterPosition
            val song = searchItems[position]
            song.isLiked = !song.isLiked

            // Update the song in your database
            FirebaseDatabaseManager.updateSongInFirebase(
                SongNew(
                    song.coverArtUrl,
                    song.songName,
                    song.artist,
                    song.id,
                    song.isLiked
                )
            ) { success ->
                if (success) {
                    // Update the UI only after the song is updated in Firebase
                    likedImageView.setImageDrawable(
                        if (song.isLiked) {
                            val drawable: Drawable? =
                                likedImageView.context.getDrawable(R.drawable.heart_filled)
                            drawable
                        } else {
                            val drawable: Drawable? =
                                likedImageView.context.getDrawable(R.drawable.heart_empty)
                            drawable
                        }
                    )

                    // Update only the clicked item
                    notifyItemChanged(position)

                    // Disable the like button for 3 seconds
                    likedImageView.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        likedImageView.isEnabled = true
                    }, 3000)
                } else {
                    // Handle the error
                    song.isLiked =
                        !song.isLiked // Revert the like status if the update operation failed
                }
            }
        }
    }

    override fun getItemCount() = searchItems.size
}