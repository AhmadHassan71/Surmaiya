package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smd.surmaiya.R
import com.smd.surmaiya.adapters.AlbumAddSongAdapter
import com.smd.surmaiya.itemClasses.SongNew

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddAlbumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAlbumFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var cancelButton: Button
    private lateinit var albumAddSongRecyclerView: RecyclerView

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
        return inflater.inflate(R.layout.fragment_add_album, container, false)
    }


    fun initializeViews() {
        cancelButton = view?.findViewById(R.id.cancelButton)!!
        albumAddSongRecyclerView = view?.findViewById(R.id.albumAddSongRecyclerView)!!
    }

    fun setUpRecyclerView() {
        // Create dummy data for testing
        val albumItems = mutableListOf(

            SongNew(
                songName="Song 2",
                artistName = "Artist 2",
                songCoverImageResource="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwN1QCgcXpfMoVDku3LvtHv1xEi5IONNOp2z_Q4IGE2TA4GCm4EXxvN9B7keyJaeWLPLA&usqp=CAU"
            ),
            SongNew(
                songName="Song 2",
                artistName = "Artist 2",
                songCoverImageResource="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwN1QCgcXpfMoVDku3LvtHv1xEi5IONNOp2z_Q4IGE2TA4GCm4EXxvN9B7keyJaeWLPLA&usqp=CAU"
            ),
            SongNew(
                songName="Song 2",
                artistName = "Artist 2",
                songCoverImageResource="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwN1QCgcXpfMoVDku3LvtHv1xEi5IONNOp2z_Q4IGE2TA4GCm4EXxvN9B7keyJaeWLPLA&usqp=CAU"
            ),

            )

        // Set the adapter for the RecyclerView
        albumAddSongRecyclerView.adapter = AlbumAddSongAdapter(albumItems)

        // Set the layout manager for the RecyclerView
        albumAddSongRecyclerView.layoutManager = LinearLayoutManager(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews() // Move this line here
        setUpOnClickListeners()
        setUpRecyclerView()
    }


    fun setUpOnClickListeners() {

        cancelButton.setOnClickListener {
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
         * @return A new instance of fragment AddAlbumFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAlbumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}