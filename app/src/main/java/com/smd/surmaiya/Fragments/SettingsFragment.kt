package com.smd.surmaiya.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.smd.surmaiya.HelperClasses.FragmentNavigationHelper
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.HelperClasses.SideBarNavigationHelper
import com.smd.surmaiya.R
import com.smd.surmaiya.activities.NotificationsActivity
import com.smd.surmaiya.activities.ResetPasswordActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setUpOnClickListeners(requireActivity())
        SideBarNavigationHelper(requireActivity()).openDrawerOnMenuClick(view, requireActivity())
        SideBarNavigationHelper(requireActivity()).setupNavigationView(requireActivity().findViewById(R.id.drawer_layout))
    }


//    private lateinit var backButton: ImageView
    fun initializeViews() {
//        backButton = view?.findViewById(R.id.backButton)!!

    }

    fun setUpOnClickListeners(activity: FragmentActivity) {
        view?.findViewById<View>(R.id.editProfileTextView)?.setOnClickListener {
            FragmentNavigationHelper(activity).loadFragment(EditProfileFragment())
        }
        view?.findViewById<View>(R.id.resetPasswordTextView)?.setOnClickListener {
            Navigator.navigateToActivity(activity, ResetPasswordActivity::class.java)
        }
        view?.findViewById<View>(R.id.notificationsTextView)?.setOnClickListener {
            Navigator.navigateToActivity(activity, NotificationsActivity::class.java)
        }
        view?.findViewById<View>(R.id.equalizerTextView)?.setOnClickListener {
            FragmentNavigationHelper(activity).loadFragment(EqualizerFragment())
        }
//        backButton.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }

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
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}