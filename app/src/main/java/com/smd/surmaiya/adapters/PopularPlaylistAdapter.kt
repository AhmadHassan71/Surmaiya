package com.smd.surmaiya.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist

class PopularPlaylistAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PopularPlaylistAdapter.PlaylistViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_popular_playlists, parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val currentPlaylist = playlists[position]

        Glide.with(holder.playlistCoverImageView.context)
            .load(currentPlaylist.coverArtUrl)
            .into(holder.playlistCoverImageView)

        holder.playlistNameTextView.text = currentPlaylist.playlistName
        val songsNumber = currentPlaylist.songids.size
        holder.songsNumberTextView.text = "${songsNumber} songs"
    }

    override fun getItemCount() = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playlistCoverImageView: ImageView = itemView.findViewById(R.id.playlistCoverImageView)
        val playlistNameTextView: TextView = itemView.findViewById(R.id.playlistNameTextView)
        val songsNumberTextView: TextView = itemView.findViewById(R.id.songsNumberTextView)
    }
}
