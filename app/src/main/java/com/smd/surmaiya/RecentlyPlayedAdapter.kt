package com.smd.surmaiya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecentlyPlayedAdapter(private val songs: List<RecentlyPlayedSong>) : RecyclerView.Adapter<RecentlyPlayedAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recently_played, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]
        holder.songCoverImageView.setImageResource(currentSong.songCoverImageResource)
        holder.songNameTextView.text = currentSong.songName
        holder.artistNameTextView.text = currentSong.artistName
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCoverImageView: ImageView = itemView.findViewById(R.id.songCoverImageView)
        val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    }
}
