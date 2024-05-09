package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.itemClasses.User

object FirebaseDatabaseManager {

    // Singleton instance
    private var instance: FirebaseDatabaseManager? = null
    private val database = FirebaseDatabase.getInstance()

    @JvmStatic
    fun getInstance(): FirebaseDatabaseManager {
        if (instance == null) {
            instance = FirebaseDatabaseManager
        }
        return instance!!
    }

    fun signUpUser(user: User, activity:AppCompatActivity, Callback: (Boolean) -> Unit){

        val myRef = database.getReference("users")
        val userId = myRef.push().key
        user.id = userId.toString()

        myRef.child(userId.toString()).setValue(user)
            .addOnSuccessListener {
                Log.d("Signing Up User In Database", "User data saved successfully")
                FirebaseAuthManager(activity).saveUserAuthentication(user)
                {
                    Callback(it)

                }

            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error saving user data: ${e.message}")
                Callback(false)

            }

    }

}

