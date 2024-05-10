package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R

class LibraryFilterAdapter(private val filters: MutableList<String>) :
    RecyclerView.Adapter<LibraryFilterAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibraryFilterAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_filter_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val textView = holder.linearLayout.findViewById<TextView>(R.id.filterText)
        textView.text = filters[position]
    }

    override fun getItemCount() = filters.size
}