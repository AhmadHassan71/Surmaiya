package com.smd.surmaiya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopGenresAdapter(private val genreList: List<TopGenres>) : RecyclerView.Adapter<TopGenresAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_top_genres, parent, false) // Use your genre_card_layout.xml
        return GenreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val currentGenre = genreList[position]
        holder.genreImageView.setImageResource(currentGenre.genreImageResource)
        holder.genreTextView.text = currentGenre.genreName
    }

    override fun getItemCount(): Int = genreList.size

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genreImageView: ImageView = itemView.findViewById(R.id.genreImageView)
        val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
    }
}
