package com.smd.surmaiya.adapters

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.SongManager
import com.smd.surmaiya.R
import com.smd.surmaiya.interfaces.OnArtistClickListener
import com.smd.surmaiya.itemClasses.Song

class SearchItemAdapter(
    private val searchItems: MutableList<Song>,
    private val onArtistClickListener: OnArtistClickListener,
    private val fragmentManager: FragmentManager // Add this line

) :
    RecyclerView.Adapter<SearchItemAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    @RequiresApi(Build.VERSION_CODES.P)
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

        songArtistNameTextView.setOnClickListener {
            val artists = searchItems[position].artist.split(",")
            val firstArtist = artists[0]
            onArtistClickListener.onArtistClick(firstArtist)
        }

        songImageView.setOnClickListener {
            val song = searchItems[position]
            MusicServiceManager.broadCastSongSelected(song)



        }


    }

    override fun getItemCount() = searchItems.size
}