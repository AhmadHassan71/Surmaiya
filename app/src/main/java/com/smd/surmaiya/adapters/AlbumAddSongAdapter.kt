package com.smd.surmaiya.adapters

import android.app.AlertDialog
import android.graphics.Color
import android.util.Log
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.Fragments.ArtistPageFragment
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.SongNew

class AlbumAddSongAdapter(private val myDataset: MutableList<SongNew>) :
    RecyclerView.Adapter<AlbumAddSongAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumAddSongAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_add_song, parent, false) as LinearLayout

        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val textView1 = holder.linearLayout.findViewById<TextView>(R.id.albumTextView1)
        val textView2 = holder.linearLayout.findViewById<TextView>(R.id.albumTextView2)
        textView1.text = myDataset[position].songName
        textView2.text = myDataset[position].artistName
        val imageView = holder.linearLayout.findViewById<ImageView>(R.id.albumImageView)
        Glide.with(holder.linearLayout.context).load(myDataset[position].songCoverImageResource)
            .into(
                imageView
            )
        holder.itemView.setBackgroundColor(Color.parseColor("#F3CC00"))
        holder.itemView.elevation = 0f
        val optionsImageView = holder.itemView.findViewById<ImageView>(R.id.optionsImageView)
        optionsImageView.setOnClickListener {
            deleteOption(optionsImageView, holder)
        }
        val likedImageView = holder.itemView.findViewById<ImageView>(R.id.likeImageView)
        FirebaseDatabaseManager.getLikedSongsFromFirebase { likedSongs ->
            // Check if the current song is in the list of liked songs
            if (myDataset[position].songId in likedSongs) {
                likedImageView.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.heart_filled))
                myDataset[position].isLiked = true
            } else {
                likedImageView.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.heart_empty))
                myDataset[position].isLiked = false
            }
        }

        likedImageView.setOnClickListener {
            likeOption(likedImageView, holder)
        }

        textView2.setOnClickListener {
            val artists = myDataset[position].artistName.split(",")
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

    }

    private fun likeOption(likedImageView: ImageView?, holder: AlbumAddSongAdapter.MyViewHolder) {
        likedImageView?.setOnClickListener {
            val position = holder.adapterPosition
            val song = myDataset[position]
            song.isLiked = !song.isLiked

            // Update the song in your database
            FirebaseDatabaseManager.updateSongInFirebase(song){ success ->
                if (success) {
                    // Update the UI only after the song is updated in Firebase
                    likedImageView.setImageDrawable(
                        if (song.isLiked) {
                            val drawable: Drawable? = likedImageView.context.getDrawable(R.drawable.heart_filled)
                            drawable
                        } else {
                            val drawable: Drawable? = likedImageView.context.getDrawable(R.drawable.heart_empty)
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
                    song.isLiked = !song.isLiked // Revert the like status if the update operation failed
                }
            }
        }
    }

    private fun deleteOption(
        optionsImageView: ImageView,
        holder: MyViewHolder
    ) {
        optionsImageView.setOnClickListener {
            builder(it, holder)!!
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun builder(
        it: View,
        holder: MyViewHolder
    ): AlertDialog.Builder? = AlertDialog.Builder(it.context)
        .setTitle("Delete Song")
        .setMessage("Do you want to delete this song from the playlist?")
        .setPositiveButton("Yes") { _, _ ->
            val songNew = myDataset[holder.adapterPosition]
            myDataset.removeAt(holder.adapterPosition)
            notifyDataSetChanged()

            val playlist = PlaylistManager.getPlaylists()
            val songIdList = playlist!!.songIds.toMutableList() // Convert to MutableList

            songIdList.remove(songNew.songId)
            playlist.songIds = songIdList

            // Update the playlist in Firebase
            updateData(playlist, it)
        }

    private fun updateData(playlist: Playlist?, it: View) {
        FirebaseDatabaseManager.updatePlaylistInFirebase(playlist!!) { success ->
            if (success) {
                CustomToastMaker().showToast(
                    it.context,
                    "Song deleted successfully"
                )
            } else {
                CustomToastMaker().showToast(
                    it.context,
                    "Failed to delete song"
                )
            }
        }
    }

    override fun getItemCount() = myDataset.size
}