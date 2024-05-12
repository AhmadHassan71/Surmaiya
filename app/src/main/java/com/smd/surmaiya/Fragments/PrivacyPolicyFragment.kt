package com.smd.surmaiya.Fragments

import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.smd.surmaiya.HelperClasses.FragmentHelper
import com.smd.surmaiya.HelperClasses.FragmentNavigationHelper
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.HelperClasses.SideBarNavigationHelper
import com.smd.surmaiya.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class PrivacyPolicyFragment: Fragment(){

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.backButton).setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        animateChildren(viewGroup = view as ViewGroup)
    }

    private fun animateChildren(viewGroup: ViewGroup) {
        val delay = 500L // delay in ms before the next view starts animating
        var currentDelay = 0L // start delay for the first view

        for (child in viewGroup.children) {
            child.alpha = 0f // make the view initially invisible
            child.animate()
                .alpha(1f) // animate to fully visible
                .setStartDelay(currentDelay)
                .setDuration(1500) // duration of the animation
                .start()

            currentDelay += delay
        }
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
            PrivacyPolicyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}