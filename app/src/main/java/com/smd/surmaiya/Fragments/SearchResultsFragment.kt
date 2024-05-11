package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.SearchFilterAdapter
import com.smd.surmaiya.adapters.SearchItemAdapter
import com.smd.surmaiya.itemClasses.SongNew

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
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var genreFilterRecyclerView: RecyclerView
    private lateinit var searchSongRecyclerView: RecyclerView
    private lateinit var searchSongAdapter: SearchItemAdapter
    private var songList: MutableList<SongNew> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment i
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setOnClickListeners()
        setUpRecyclerView()
    }

    fun setUpRecyclerView() {
        // Create dummy data for testing
        val filterItems = mutableListOf(
            "Songs",
            "Playlists",
            "Artists",
            "Albums",

            )
        filterRecyclerView.adapter = SearchFilterAdapter(filterItems)
        filterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        searchSongRecyclerView = view?.findViewById(R.id.searchResultsRecyclerView)!!
        songList = mutableListOf(
            SongNew(
                "https://preview.redd.it/the-full-key-visual-for-bleach-tybw-the-separation-v0-ifguzaidwgkb1.jpg?auto=webp&s=c3c7385837b8d5f1f449a989320cd15cc4eef49e",
                "Song 1",
                "Faraz Deutsch","1"
            ),
            SongNew(
                "https://preview.redd.it/the-full-key-visual-for-bleach-tybw-the-separation-v0-ifguzaidwgkb1.jpg?auto=webp&s=c3c7385837b8d5f1f449a989320cd15cc4eef49e",
                "Song 2",
                "Ahmad Deutsch","2"
            ),
        )
        searchSongAdapter = SearchItemAdapter(songList)
        searchSongRecyclerView.adapter = searchSongAdapter
        searchSongRecyclerView.layoutManager = LinearLayoutManager(context)

        // Create dummy data for testing
        val genreFilterItems = mutableListOf(
            "Rock", "R&B", "HipHop", "Metal", "Pop", "Jazz"
        )
        genreFilterRecyclerView.adapter = SearchFilterAdapter(genreFilterItems)
        genreFilterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        filterRecyclerView = view?.findViewById(R.id.typeFilterRecyclerView)!!
        genreFilterRecyclerView = view?.findViewById(R.id.genreFilterRecyclerView)!!
    }


    fun setOnClickListeners() {
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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