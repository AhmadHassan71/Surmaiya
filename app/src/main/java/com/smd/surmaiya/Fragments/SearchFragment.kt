package com.smd.surmaiya.Fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.GenreAdapter
import com.smd.surmaiya.itemClasses.Genre


class SearchFragment : Fragment() {

    private lateinit var searchBar: SearchView
    private lateinit var genreRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchBar = view.findViewById(R.id.searchView)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    return false
                }
                val searchResultsFragment = SearchResultsFragment()
                val bundle = Bundle()
                bundle.putString("search_query", query)
                searchResultsFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, searchResultsFragment)
                    .addToBackStack(null)
                    .commit()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isEmpty()) {
                    return false
                }
                val searchResultsFragment = SearchResultsFragment()
                val bundle = Bundle()
                bundle.putString("search_query", newText)
                searchResultsFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, searchResultsFragment)
                    .addToBackStack(null)
                    .commit()
                return false
            }
        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intializedViews(view)
        setupRecyclerView()
    }

    private fun intializedViews(view: View) {
        genreRecyclerView = view.findViewById(R.id.GenresRecyclerView)
    }

    private fun setupRecyclerView() {
        val genresList = mutableListOf<Genre>()
        val genreslistAdapter = GenreAdapter(genresList)
        genreRecyclerView.layoutManager = GridLayoutManager(context, 2)
        genreRecyclerView.adapter = GenreAdapter(genresList)
        FirebaseDatabaseManager.getAllGenres { genres ->
            for (genre in genres) {
                val newGenre = Genre(genre, "")
                genresList.add(newGenre)
            }
        }
        FirebaseDatabaseManager.getAllSongs { songs ->
            for (song in songs) {
                for (genre in genresList) {
                    if (song.genres.contains(genre.name)) {
                        genre.imageUrl = song.coverArtUrl
                    }
                }
            }
            genreRecyclerView.adapter = GenreAdapter(genresList)
            genreslistAdapter.notifyDataSetChanged()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}