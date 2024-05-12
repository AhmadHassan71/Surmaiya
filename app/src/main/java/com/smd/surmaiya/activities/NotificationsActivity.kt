package com.smd.surmaiya.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.adapters.NotificationsAdapter
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.notifications

class NotificationsActivity : AppCompatActivity() {
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var notificationsList: MutableList<notifications>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)

        initializeRecyclerView()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initializeRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.notificationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // get notifications from firebase
        notificationsList = mutableListOf()

        fetchAllUserNotifications()
        recyclerView.adapter = NotificationsAdapter(notificationsList)
        notificationsAdapter = recyclerView.adapter as NotificationsAdapter
    }

    private fun fetchAllUserNotifications() {
        val currentUser = UserManager.getCurrentUser()
        currentUser?.let { user ->
            val myDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.id).child("notifications")

            myDatabase.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val notification = dataSnapshot.child("notification").value.toString()
                    val message = dataSnapshot.child("message").value.toString()
                    val notificationId= dataSnapshot.key.toString()
                    notification.let {
                        notificationsList.add(notifications(notification, message))
                        notificationsAdapter.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // Handle changes to notifications
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    // Handle removed notifications
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // Handle moved notifications
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }



}