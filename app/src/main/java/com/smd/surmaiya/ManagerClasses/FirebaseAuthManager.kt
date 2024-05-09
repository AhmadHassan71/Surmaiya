package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.itemClasses.User
import com.google.firebase.messaging.FirebaseMessaging
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import kotlinx.coroutines.flow.callbackFlow

class FirebaseAuthManager(private val activity: AppCompatActivity) {

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    fun saveUserAuthentication(user: User,Callback:(Boolean)->Unit)
    {
        mAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user1 = mAuth.currentUser
                    UserManager.setCurrentUser(user)
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@addOnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        // Log and toast
                        val msg = token
                        Log.d("MyToken", msg)
                        addFcmTokenToUser(UserManager.getCurrentUser()?.id.toString(), "users", token)
                        UserManager.getCurrentUser()?.fcmToken = token.toString()
                        CustomToastMaker().showToast(activity, "User created successfully")
                        Callback(true)
                    }


                } else {
                    Log.e("Signing Up User", "Error creating user: ${task.exception?.message}")
                    Callback(false)

                }
            }

    }

    fun addFcmTokenToUser(
        userId: String,
        userType: String,
        fcmToken: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val fcmTokenRef = database.getReference(userType).child(userId).child("fcmToken")

        fcmTokenRef.setValue(fcmToken)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "FCM token added successfully")
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Failed to add FCM token: ${e.message}")
            }
    }
}