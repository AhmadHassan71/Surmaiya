package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.itemClasses.ListItem
import com.smd.surmaiya.R

class ListItemAdapter(private val listItems: List<ListItem>) :
    RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_generic, parent, false) // Use your list_item_layout.xml
        return ListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val currentItem = listItems[position]
        holder.primaryImage.setImageResource(currentItem.imageResource)
        holder.primaryTextView.text = currentItem.primaryText
        holder.secondaryTextView.text = currentItem.secondaryText
    }

    override fun getItemCount(): Int = listItems.size

    inner class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val primaryImage: ImageView = itemView.findViewById(R.id.primaryImage)
        val primaryTextView: TextView = itemView.findViewById(R.id.primaryTextView)
        val secondaryTextView: TextView = itemView.findViewById(R.id.secondaryTextView)
    }
}
