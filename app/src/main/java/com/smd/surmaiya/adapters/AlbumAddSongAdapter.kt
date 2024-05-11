package com.smd.surmaiya.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.SongNew

class AlbumAddSongAdapter(private val myDataset: MutableList<SongNew>) :
    RecyclerView.Adapter<AlbumAddSongAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AlbumAddSongAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_add_song, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val textView1 = holder.linearLayout.findViewById<TextView>(R.id.albumTextView1)
        val textView2 = holder.linearLayout.findViewById<TextView>(R.id.albumTextView2)
        textView1.text = myDataset[position].songName
        textView2.text = myDataset[position].artistName
        val imageView = holder.linearLayout.findViewById<ImageView>(R.id.albumImageView)
        Glide.with(holder.linearLayout.context).load(myDataset[position].songCoverImageResource).into(
            imageView
        )
        holder.itemView.setBackgroundColor(Color.parseColor("#F3CC00"))
        holder.itemView.elevation = 0f

    }

    override fun getItemCount() = myDataset.size
}