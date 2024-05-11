package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.SongNew

class SearchItemAdapter(private val searchItems: MutableList<Song>) :
    RecyclerView.Adapter<SearchItemAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchItemAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songNameTextView = holder.linearLayout.findViewById<TextView>(R.id.textView1)
        val songArtistNameTextView = holder.linearLayout.findViewById<TextView>(R.id.textView2)
        val songImageView = holder.linearLayout.findViewById<ImageView>(R.id.songCoverImageView)
        songNameTextView.text = searchItems[position].songName
        songArtistNameTextView.text = searchItems[position].artist
        if (searchItems[position].coverArtUrl != null) {
            Glide.with(holder.linearLayout.context)
                .load(searchItems[position].coverArtUrl).into(songImageView)
        }
    }

    override fun getItemCount() = searchItems.size
}