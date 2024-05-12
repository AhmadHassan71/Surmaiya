package com.smd.surmaiya.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.SongNew

class AddToPlaylistAdapter(private val myDataset: MutableList<SongNew>) :
    RecyclerView.Adapter<AddToPlaylistAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.albumTextView1)
        val artistName: TextView = view.findViewById(R.id.albumTextView2)
        val songCover: ImageView = view.findViewById(R.id.albumImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_add_song, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = myDataset[position]
        holder.songName.text = song.songName
        holder.artistName.text = song.artistName
        Glide.with(holder.view.context).load(song.songCoverImageResource).into(holder.songCover)

        // Change the background color and elevation based on whether the song is selected
        if (song.isSelected) {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = 15f // set the corner radius you want (in pixels)
            drawable.setColor(Color.parseColor("#F3CC00"))
            holder.view.background = drawable
            holder.view.elevation = 10f // set the elevation you want (in pixels)
        } else {
            holder.view.setBackgroundColor(Color.parseColor("#F3CC00"))
            holder.view.elevation = 0f
        }

        // Set an OnClickListener for the entire item view
        holder.view.setOnClickListener {
            song.isSelected = !song.isSelected
            notifyItemChanged(position) // Notify the adapter that the item at 'position' has changed
        }
    }

    override fun getItemCount() = myDataset.size
}