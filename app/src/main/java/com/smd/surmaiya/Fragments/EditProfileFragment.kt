package com.smd.surmaiya.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import com.smd.surmaiya.HelperClasses.ImageUploadUtils
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.FirebaseStorageManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.UserType
import java.io.ByteArrayOutputStream
import java.util.Collections
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var nameText: TextView
    private lateinit var phoneEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var imageView17: ImageView
    private lateinit var userProfilePicture:ImageView
    private lateinit var saveChangesButton:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setUpOnClickListeners()
        setupCountrySpinner()
        setUserDetails()

    }

    private fun initializeViews(view: View) {
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneEditText= view.findViewById(R.id.birthEditText)
        countrySpinner = view.findViewById(R.id.countryEditText)
        imageView17 = view.findViewById(R.id.imageView17)
        userProfilePicture=view.findViewById(R.id.imageView16)
        saveChangesButton=view.findViewById(R.id.loginButton)

        if(UserManager.getCurrentUser()!!.userType == UserType.ARTIST)
        {
            nameEditText.visibility=View.GONE
            view.findViewById<TextView>(R.id.name_text).visibility=View.GONE
        }
    }

    private fun setUserDetails()
    {
        val user= UserManager.getCurrentUser()
        nameEditText.setText(user?.name)
        emailEditText.setText(user?.email)
        phoneEditText.setText(user?.phone)
        countrySpinner.setSelection(countryList().indexOf(user?.country))

        Glide.with(this)
            .load(UserManager.getCurrentUser()?.profilePictureUrl)
            .placeholder(R.mipmap.melisa)
            .into((userProfilePicture))

    }

    private fun countryList(): List<String> {
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.length > 0 && !countries.contains(country)) {
                countries.add(country)
            }
        }
        Collections.sort(countries)
        countries.add(0, "Select A Country")
        return countries
    }


    private fun setupCountrySpinner() {
        val countrySpinner: Spinner = requireView().findViewById<Spinner>(R.id.countryEditText)
        val countries = countryList()
        val adapter = object : ArrayAdapter<String>(view?.context!!, R.layout.spinner_item, countries) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                view.isEnabled = position != 0 // Disable the first item
                return view
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item) // Set the drop-down view to spinner_item
        countrySpinner.adapter = adapter
    }

    private fun setUpOnClickListeners() {
        imageView17.setOnClickListener {
            ImageUploadUtils.showImageUploadDialog(this)
        }

        saveChangesButton.setOnClickListener {
            saveChanges()
        }

        val backButton = requireView().findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun saveChanges() {
        val user = UserManager.getCurrentUser()

        val newName = nameEditText.text.toString()
        val newEmail = emailEditText.text.toString()
        val newPhone = phoneEditText.text.toString()
        val newCountry = countrySpinner.selectedItem.toString()

        var changesMade = false

        if (user?.name != newName && newName.isNotEmpty() && !newName.matches(Regex(".*\\d.*")) ) {
            user?.name = newName
            changesMade = true
        }

        if (user?.email != newEmail && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            user?.email = newEmail
            changesMade = true
        }

        if (user?.phone != newPhone && newPhone.matches(Regex("^[+]?[0-9]{10,13}\$"))) {
            user?.phone = newPhone
            changesMade = true
        }

        if (user?.country != newCountry && newCountry != "Select A Country") {
            user?.country = newCountry
            changesMade = true
        }

        if (changesMade) {
            if (user != null) {
                FirebaseDatabaseManager.getInstance().updateUser(user) { success ->
                    if (success) {
                        CustomToastMaker().showToast(requireContext(), "Changes saved successfully")
                    } else {

                        CustomToastMaker().showToast(requireContext(), "Failed to save changes")
                    }
                }
            }
        } else {
            // Show custom toast message
           CustomToastMaker().showToast(requireContext(), "No changes made")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageUploadUtils.handleImageUploadResult(requestCode, resultCode, data, this, requireView()) { imagePath ->
            var currentUserImage= userProfilePicture.drawable
            UserManager.getCurrentUser()?.profilePictureUrl = imagePath
            FirebaseDatabaseManager.getInstance().updateUser(UserManager.getCurrentUser()!!) { success ->
                if (success) {
                    Glide.with(this)
                        .load(imagePath)
                        .placeholder(currentUserImage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Enable disk cache
                        .into((userProfilePicture))

                    CustomToastMaker().showToast(requireContext(), "Profile picture updated successfully")
                } else {
                    CustomToastMaker().showToast(requireContext(), "Failed to update profile picture")
                }
            }
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
    }
}