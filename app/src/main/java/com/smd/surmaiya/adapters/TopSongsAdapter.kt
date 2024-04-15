package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song

class TopSongsAdapter(private val songs: List<Song>) : RecyclerView.Adapter<TopSongsAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_monthly_ranking, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]
        holder.songImageView.setImageResource(currentSong.songCoverImageResource)
        holder.songNameTextView.text = currentSong.songName
        holder.artistNameTextView.text = currentSong.artistName
    }

    override fun getItemCount() = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImageView: ImageView = itemView.findViewById(R.id.songImageView)
        val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    }
}
