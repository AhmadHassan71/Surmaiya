package com.smd.surmaiya.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.FilterItem

class SearchFilterAdapter(
    private val filters: MutableList<FilterItem>,
    private val onGenreClick: (FilterItem) -> Unit
) : RecyclerView.Adapter<SearchFilterAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_filter_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val textView = holder.linearLayout.findViewById<TextView>(R.id.filterText)
        val filterItem = filters[position]
        textView.text = filterItem.filter
        holder.linearLayout.setOnClickListener {
            filterItem.isSelected = !filterItem.isSelected
            it.setBackgroundResource(if (filterItem.isSelected) R.drawable.tag_selected else R.drawable.tag_unselected)
            textView.setTextColor(if (filterItem.isSelected) Color.parseColor("#11572B") else Color.WHITE)
            onGenreClick(filterItem)
        }
    }

    fun getSelectedGenres(): List<String> {
        return filters.filter { it.isSelected }.map { it.filter }
    }

    override fun getItemCount() = filters.size
}