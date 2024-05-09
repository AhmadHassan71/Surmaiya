package com.smd.surmaiya.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.smd.surmaiya.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddToPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddToPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var backButton: ImageView
    private lateinit var createNewPlaylist: LinearLayout
    private lateinit var searchPlaylist: LinearLayout
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
        return inflater.inflate(R.layout.fragment_add_to_playlist, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners()
    }

    fun initializeViews() {
        backButton = view?.findViewById(R.id.backButton)!!
        createNewPlaylist = view?.findViewById(R.id.createNewPlaylist)!!
        searchPlaylist = view?.findViewById(R.id.searchPlaylist)!!
    }

    fun setUpOnClickListeners() {

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        createNewPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateNewPlaylistFragment()).addToBackStack(null).commit()
        }
        searchPlaylist.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlaylistSearchFragment()).addToBackStack(null).commit()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddToPlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddToPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}