package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.Fragments.ArtistPageFragment
import com.smd.surmaiya.Fragments.PlaylistSearchFragment
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.ManagerClasses.PlaylistManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song

class TopSongsAdapter(private val songs: List<Song>, private val isMonthlyRanking: Boolean, private val playlists: List<Playlist>) : RecyclerView.Adapter<TopSongsAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_monthly_ranking, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]

        if (isMonthlyRanking) {
            // Load the song image and set the song and artist names for monthly ranking
            Glide.with(holder.itemView.context)
                .load(currentSong.coverArtUrl)
                .into(holder.songImageView)
            holder.songNameTextView.text = currentSong.songName
            holder.artistNameTextView.text = currentSong.artist
        } else {
            // Load the playlist image and set the playlist name and description for popular playlists
            Glide.with(holder.itemView.context)
                .load(currentSong.coverArtUrl)
                .into(holder.songImageView)
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