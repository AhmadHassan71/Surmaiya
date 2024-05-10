package com.smd.surmaiya.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YourUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourUserFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_your_user, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        prepareClickListeners(view)
        prepareUserInformation(view)


    }

    private fun prepareClickListeners(view: View) {
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val editProfileButton = view.findViewById<ImageView>(R.id.dotsButton)
        editProfileButton.setOnClickListener {
            val editProfileFragment = EditProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun prepareUserInformation(view: View) {
        val profilePic = view.findViewById<ImageView>(R.id.yourUserProfilePicture)
        Glide.with(requireActivity())
            .load(UserManager.getCurrentUser()?.profilePictureUrl)
            .into(profilePic)

        val username = view.findViewById<TextView>(R.id.username)
        username.text = UserManager.getCurrentUser()?.name

        val email = view.findViewById<TextView>(R.id.userEmail)
        email.text = UserManager.getCurrentUser()?.email

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YourUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YourUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}