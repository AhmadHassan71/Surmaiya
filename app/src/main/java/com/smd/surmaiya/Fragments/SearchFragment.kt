package com.smd.surmaiya.Fragments

import BottomNavigationHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.smd.surmaiya.R


class SearchFragment : Fragment() {

    private lateinit var searchBar: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_search, container, false)
        searchBar=view.findViewById(R.id.searchView)

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SearchResultsFragment())
                    .addToBackStack(null)
                    .commit()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return view
    }

}