package com.smd.surmaiya.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import java.util.Collections
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var nameText: TextView
    private lateinit var phoneEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var imageView17: ImageView

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
    }

    private fun setUserDetails()
    {
        val user= UserManager.getCurrentUser()
        nameEditText.setText(user?.name)
        emailEditText.setText(user?.email)
        phoneEditText.setText(user?.phone)
        countrySpinner.setSelection(countryList().indexOf(user?.country))


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
            showPictureDialog()
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
    }
}