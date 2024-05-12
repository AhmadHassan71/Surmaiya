package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Album

class AlbumAdapter(private val albums: List<Album>, private val listener: AlbumAdapter.OnItemClickListener) :
    RecyclerView.Adapter<AlbumAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_playlists, parent, false) // Use your playlist_card_layout.xml
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val currentPlaylist = albums[position]
        Glide.with(holder.albumImageView.context)
            .load(currentPlaylist.coverArtUrl)
            .into(holder.albumImageView)
        holder.albumNameTextView.text = currentPlaylist.name
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = albums.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemChanged(position: Int)
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImageView: ImageView = itemView.findViewById(R.id.playlistImageView)
        val albumNameTextView: TextView = itemView.findViewById(R.id.playlistNameTextView)
    }
}