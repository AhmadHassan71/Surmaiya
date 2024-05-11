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
import com.smd.surmaiya.R


class SearchFragment : Fragment() {

    private lateinit var searchBar: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchBar = view.findViewById(R.id.searchView)
        searchBar.postDelayed(Runnable { show_keyboard(requireActivity(), searchBar) }, 50)

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
    private fun show_keyboard(activity: FragmentActivity, searchView: SearchView) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        searchView.requestFocus()
        inputMethodManager.showSoftInput(searchView, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}