package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.PlaylistSearchItem

class PlaylistSearchItemAdapter(private val dataset: MutableList<PlaylistSearchItem>) :
    RecyclerView.Adapter<PlaylistSearchItemAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.imageView)
        val titleTextView: TextView = view.findViewById(R.id.textView1)
        val subtitleTextView: TextView = view.findViewById(R.id.textView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_search_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleTextView.text = item.title
        holder.subtitleTextView.text = item.subtitle
        Glide.with(holder.imageView.context).load(item.imageUrl).into(holder.imageView)
    }

    override fun getItemCount() = dataset.size
}