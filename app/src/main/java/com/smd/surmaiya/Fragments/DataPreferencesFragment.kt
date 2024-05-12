package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.R

class DataPreferencesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("")
            param2 = it.getString("")
        }
    }

    private fun setupRequestDeletion(view: View, container: ViewGroup) {
        val requestDeletionTextView = view.findViewById<TextView>(R.id.request_deletion)
        val button = view.findViewById<Button>(R.id.delete_button)
        val infoText = view.findViewById<TextView>(R.id.deletion_info)

        FragmentHelper(requireActivity().supportFragmentManager,requireContext()).setupVisibilityToggleWithTransition(requestDeletionTextView, container, button, infoText)
        button.setOnClickListener {
            CustomToastMaker().showToast(requireContext(), "Request Sent Successfully")
        }
    }

    private fun setupRequestData(view: View, container: ViewGroup) {
        val requestDataTextView = view.findViewById<TextView>(R.id.request_data)
        val dataButton = view.findViewById<Button>(R.id.request_button)
        val dataInfoText = view.findViewById<TextView>(R.id.request_info)

        FragmentHelper(requireActivity().supportFragmentManager,requireContext()).setupVisibilityToggleWithTransition(requestDataTextView, container, dataButton, dataInfoText)
        dataButton.setOnClickListener {
            CustomToastMaker().showToast(requireContext(), "Request Sent Successfully")
        }
    }

    private fun setupCheckboxListener(view: View, container: ViewGroup) {
        val checkboxPreference1 = view.findViewById<CheckBox>(R.id.checkbox_preference1)
        val checkboxPreference2 = view.findViewById<CheckBox>(R.id.checkbox_preference2)
        val checkboxPreference3 = view.findViewById<CheckBox>(R.id.checkbox_preference3)
        val checkboxPreference4 = view.findViewById<CheckBox>(R.id.checkbox_preference4)
        val checkboxPreference5 = view.findViewById<CheckBox>(R.id.checkbox_preference5)
        val updatePrefButton = view.findViewById<Button>(R.id.update_pref_button)

        FragmentHelper(requireActivity().supportFragmentManager,requireContext()).setupCheckboxListener(checkboxPreference1, checkboxPreference2, checkboxPreference3, button = updatePrefButton)
        updatePrefButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(container, Fade())
            updatePrefButton.visibility = View.GONE
            CustomToastMaker().showToast(requireContext(), "Preferences Updated")
        }
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data_preferences, container, false)

        setupRequestDeletion(view, container!!)
        setupRequestData(view, container)
        setupCheckboxListener(view, container)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString("ARG_PARAM1", param1)
                    putString("ARG_PARAM2", param2)
                }
            }
    }
}
