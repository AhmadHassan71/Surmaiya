package com.smd.surmaiya.adapters

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.SongNew

class AlbumAddSongAdapter(private val myDataset: MutableList<SongNew>) :
    RecyclerView.Adapter<AlbumAddSongAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AlbumAddSongAdapter.MyViewHolder {
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
        Glide.with(holder.linearLayout.context).load(myDataset[position].songCoverImageResource).into(
            imageView
        )
        holder.itemView.setBackgroundColor(Color.parseColor("#F3CC00"))
        holder.itemView.elevation = 0f
        val optionsImageView = holder.itemView.findViewById<ImageView>(R.id.optionsImageView)
        optionsImageView.setOnClickListener {
            optionsImageView.setOnClickListener {
                AlertDialog.Builder(it.context)
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
                        FirebaseDatabaseManager.updatePlaylistInFirebase(playlist) { success ->
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
                    .setNegativeButton("No", null)
                    .show()
            }
        }


    }

    override fun getItemCount() = myDataset.size
}