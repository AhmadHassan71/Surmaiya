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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smd.surmaiya.Fragments.OtherUserFragment
import com.smd.surmaiya.Fragments.SearchResultsFragment
import com.smd.surmaiya.ManagerClasses.GenreManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.Genre

class GenreAdapter(private val genreList: List<Genre>) :
    RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {
    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genreName: TextView = itemView.findViewById(R.id.genre_name)
        val genreImage: ImageView = itemView.findViewById(R.id.genre_image)
        val genreBackground: ConstraintLayout = itemView.findViewById(R.id.genre_background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_item, parent, false)
        return GenreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val currentItem = genreList[position]
        holder.genreName.text = currentItem.name
        if (currentItem.imageUrl != "") {
            loadAndSetImage(holder, currentItem)
        }
        holder.genreBackground.setOnClickListener {
            GenreManager.setGenre(currentItem)
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
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
                    holder.genreImage.setImageBitmap(resource)
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
            ViewCompat.setBackgroundTintList(holder.genreBackground, ColorStateList.valueOf(color))
        }
    }

    override fun getItemCount() = genreList.size
}