package com.smd.surmaiya.ManagerClasses

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.R
import com.smd.surmaiya.activities.LoginOrSignupActivity
import com.smd.surmaiya.itemClasses.User
import com.smd.surmaiya.itemClasses.getUserWithEmail


object UserManager {


    // Singleton instance
    private var instance: UserManager? = null
    @JvmStatic
    fun getInstance(): UserManager {
        if (instance == null) {
            instance = UserManager
        }
        return instance!!
    }


    //retrieve user logged in from shared preferences

    fun saveUserLoggedInSP(isLoggedIn:Boolean, sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", isLoggedIn)
        editor.apply()
    }

    fun saveUserEmailSP(email:String, sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL", email)
        editor.apply()
    }

    //retrieve user logged in from shared preferences

    fun getUserLoggedInSP(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    fun getUserEmailSP(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString("EMAIL", null)
    }

    private lateinit var currentUser: User

    fun getCurrentUser(): User? {
        return if (::currentUser.isInitialized) currentUser else null
    }


    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun setUserProfilePictureUrl(url: String) {
        this.currentUser.profilePictureUrl = url
    }

    fun getUserProfilePictureUrl(): String {
        return this.currentUser.profilePictureUrl
    }

    fun logUser() {
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.id}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.name}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.email}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.country}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.phone}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.profilePictureUrl}")
    }

    fun fetchAndSetCurrentUser(email: String, callback: () -> Unit) {
        getUserWithEmail(email) { user ->
            if (user != null) {
                currentUser = user
                logUser()
                setCurrentUser(user)
                Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.id}")
                callback.invoke() // Execute the callback function
            }
        }

    }
    fun showGuestDialog(activity: AppCompatActivity) {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_guest_user, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
        val signUpOrLoginButton = dialogView.findViewById<Button>(R.id.signUpOrLoginButton)
        signUpOrLoginButton.setOnClickListener {
            val intent = Intent(activity, LoginOrSignupActivity::class.java)
            activity.startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }


}