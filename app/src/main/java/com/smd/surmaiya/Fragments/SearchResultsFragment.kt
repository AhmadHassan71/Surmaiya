package com.smd.surmaiya.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.showKeyboard
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.SearchFilterAdapter
import com.smd.surmaiya.adapters.SearchItemAdapter
import com.smd.surmaiya.itemClasses.FilterItem
import com.smd.surmaiya.itemClasses.Song

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var backButton: ImageView
    private lateinit var searchView: SearchView
    private lateinit var genreFilterRecyclerView: RecyclerView
    private lateinit var searchSongRecyclerView: RecyclerView
    private lateinit var searchSongAdapter: SearchItemAdapter
    private var selectedGenres = mutableListOf<String>()
    private var songList: MutableList<Song> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SearchFragment())
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment i
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setOnClickListeners()
        setUpRecyclerView()
        searchView.postDelayed(Runnable { show_keyboard(requireActivity(),searchView )} , 50)
        val query = arguments?.getString("search_query")
        if (query != null) {
            searchSongs(query, selectedGenres)
        }
    }



    fun setUpRecyclerView() {
        // Create dummy data for testing
        val genreFilterItems = mutableListOf<String>().map { FilterItem(it) }.toMutableList()

        genreFilterRecyclerView.adapter =
            SearchFilterAdapter(genreFilterItems, ::handleGenreSelection)


        FirebaseDatabaseManager.getAllGenres { genres ->
            genreFilterItems.clear()
            genreFilterItems.addAll(genres.map { FilterItem(it) })
            genreFilterRecyclerView.adapter =
                SearchFilterAdapter(genreFilterItems, ::handleGenreSelection)
        }
        genreFilterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        searchSongRecyclerView = view?.findViewById(R.id.searchResultsRecyclerView)!!
        songList = mutableListOf(
        )
        searchSongAdapter = SearchItemAdapter(songList)
        searchSongRecyclerView.adapter = searchSongAdapter
        searchSongRecyclerView.layoutManager = LinearLayoutManager(context)


    }

    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        genreFilterRecyclerView = view?.findViewById(R.id.genreFilterRecyclerView)!!
        searchView = view?.findViewById(R.id.searchView)!!
        searchView.setQuery(arguments?.getString("search_query"), false)
    }

    fun setOnClickListeners() {
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val selectedGenres =
                        (genreFilterRecyclerView.adapter as SearchFilterAdapter).getSelectedGenres()
                            .toMutableList()
                    searchSongs(query, selectedGenres)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val selectedGenres =
                        (genreFilterRecyclerView.adapter as SearchFilterAdapter).getSelectedGenres()
                            .toMutableList()
                    searchSongs(newText, selectedGenres)
                }
                return false
            }
        })
    }
    private  fun show_keyboard(activity: FragmentActivity, searchView: SearchView) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        searchView.requestFocus()
        inputMethodManager.showSoftInput(searchView, 0)
    }

    private fun handleGenreSelection(filterItem: FilterItem) {
        if (filterItem.isSelected) {
            selectedGenres.add(filterItem.filter)
            searchSongs(searchView.query.toString(), selectedGenres)
        } else {
            if (selectedGenres.contains(filterItem.filter)) {
                selectedGenres.remove(filterItem.filter)
                searchSongs(searchView.query.toString(), selectedGenres)
            }
        }
    }

    fun searchSongs(query: String, selected_genres: MutableList<String>) {
        FirebaseDatabaseManager.getAllSongs { songs ->
            val searchSongs = songs.filter { it ->
                it.songName.contains(query, ignoreCase = true) || it.genres.any {
                    it.contains(
                        query,
                        ignoreCase = true
                    )
                } || it.artist.contains(query, ignoreCase = true) || it.albumName.contains(
                    query,
                    ignoreCase = true
                )
            }
            val filteredSongs = searchSongs.filter { it ->
                selected_genres.isEmpty() || it.genres.any { selected_genres.contains(it) }
            }
            songList.clear()
            songList.addAll(filteredSongs)
            searchSongAdapter.notifyDataSetChanged()
        }


    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = SearchResultsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}