package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.smd.surmaiya.itemClasses.User

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

    fun logUser(user: User){
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.id}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.name}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.email}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.country}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.phone}")
        Log.d(ContentValues.TAG, "loadUserInformation: ${currentUser.profilePictureUrl}")
    }

    fun fetchAndSetCurrentUser(email: String, context: Context, callback: () -> Unit) {



    }
}