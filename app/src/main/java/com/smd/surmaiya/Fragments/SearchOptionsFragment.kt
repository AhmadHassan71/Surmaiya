package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.SearchItemAdapter
import com.smd.surmaiya.itemClasses.SongNew

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchOptionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchSongRecyclerView: RecyclerView
    private lateinit var searchSongAdapter: SearchItemAdapter
    private var songList: MutableList<SongNew> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_options, container, false)
    }

    fun initializeViews() {
        searchSongRecyclerView = view?.findViewById(R.id.SearchItemRecyclerView)!!
        songList = mutableListOf(
            SongNew(
                "https://preview.redd.it/the-full-key-visual-for-bleach-tybw-the-separation-v0-ifguzaidwgkb1.jpg?auto=webp&s=c3c7385837b8d5f1f449a989320cd15cc4eef49e",
                "Song 1",
                "Faraz Deutsch"
            ),
            SongNew(
                "https://preview.redd.it/the-full-key-visual-for-bleach-tybw-the-separation-v0-ifguzaidwgkb1.jpg?auto=webp&s=c3c7385837b8d5f1f449a989320cd15cc4eef49e",
                "Song 2",
                "Ahmad Deutsch"
            ),
        )
        searchSongAdapter = SearchItemAdapter(songList)
        searchSongRecyclerView.adapter = searchSongAdapter
        searchSongRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchOptions.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchOptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}