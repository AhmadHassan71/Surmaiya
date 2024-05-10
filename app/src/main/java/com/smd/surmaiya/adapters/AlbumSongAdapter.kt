package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.SongNew
import de.hdodenhof.circleimageview.CircleImageView

class AlbumSongAdapter(private val songList: MutableList<SongNew>) :
    RecyclerView.Adapter<AlbumSongAdapter.SongViewHolder>() {
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: CircleImageView = itemView.findViewById(R.id.song_image)
        val textView: TextView = itemView.findViewById(R.id.song_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.album_song_item, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentItem = songList[position]
        Glide.with(holder.imageView.context).load(currentItem.songCoverImageResource).into(
            holder.imageView
        )
        holder.textView.text = currentItem.songName
    }
    override fun getItemCount() = songList.size
}
