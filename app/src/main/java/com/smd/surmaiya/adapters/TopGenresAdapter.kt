package com.smd.surmaiya.adapters

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smd.surmaiya.Fragments.SearchResultsFragment
import com.smd.surmaiya.ManagerClasses.GenreManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Genre

class TopGenresAdapter(private val genreList: List<Genre>) :
    RecyclerView.Adapter<TopGenresAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_top_genres, parent, false) // Use your genre_card_layout.xml
        return GenreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val currentGenre = genreList[position]
        holder.genreTextView.text = currentGenre.name
        if (currentGenre.imageUrl != "") {
            loadAndSetImage(holder, currentGenre)
        }
        holder.genreCardView.setOnClickListener {
            GenreManager.setGenre(currentGenre)
            val fragmentManager =
                (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, SearchResultsFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

    }

    private fun loadAndSetImage(holder: GenreViewHolder, currentItem: Genre) {
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(currentItem.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.genreImageView.setImageBitmap(resource)
                    setPaletteColors(resource, holder)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun setPaletteColors(bitmap: Bitmap, holder: GenreViewHolder) {
        Palette.from(bitmap).generate { palette ->
            val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
            val color = swatch?.rgb ?: Color.MAGENTA
            ViewCompat.setBackgroundTintList(holder.genreCardView, ColorStateList.valueOf(color))
        }
    }


    override fun getItemCount(): Int = genreList.size

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genreImageView: ImageView = itemView.findViewById(R.id.genreImageView)
        val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
        val genreCardView: View = itemView.findViewById(R.id.genre_card)
    }
}
