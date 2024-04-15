package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.R

class PlaylistAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_playlists, parent, false) // Use your playlist_card_layout.xml
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val currentPlaylist = playlists[position]
        holder.playlistImageView.setImageResource(currentPlaylist.imageResource)
        holder.playlistNameTextView.text = currentPlaylist.playlistName
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playlistImageView: ImageView = itemView.findViewById(R.id.playlistImageView)
        val playlistNameTextView: TextView = itemView.findViewById(R.id.playlistNameTextView)
    }
}
