package com.smd.surmaiya.ManagerClasses

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smd.surmaiya.itemClasses.User
import com.google.firebase.messaging.FirebaseMessaging
import com.smd.surmaiya.HelperClasses.CustomToastMaker
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest


class FirebaseAuthManager(private val activity: AppCompatActivity) {

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
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

    fun loginUser(email: String,password1:String, Callback: (Boolean) -> Unit)
    {
        val password = hashPassword(password1)

        Log.d("Password",password)


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser

                    val userManager = UserManager.getInstance()
                    userManager.fetchAndSetCurrentUser(user?.email.toString())
                    {
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
                            CustomToastMaker().showToast(activity, "User logged in successfully")
                            Callback(true)
                        }

                    }
                } else {

                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Callback(false)

                }
            }
    }

    fun checkEmailAvailability(email: String, completion: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAvailable = snapshot.childrenCount.toInt() == 0
                completion(isAvailable)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                completion(false)
            }
        })
    }

    fun checkPhoneAvailability(phone: String, completion: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAvailable = snapshot.childrenCount.toInt() == 0
                completion(isAvailable)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                completion(false)
            }
        })
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

