package com.smd.surmaiya.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.smd.surmaiya.Fragments.EditProfileFragment
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.NotificationsManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.R
import com.smd.surmaiya.Services.MusicService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        if (UserManager.getInstance().getUserLoggedInSP(getSharedPreferences("USER_LOGIN", MODE_PRIVATE))) {

            UserManager.getInstance()
                .getUserEmailSP(getSharedPreferences("USER_LOGIN", MODE_PRIVATE))?.let {

                    // Bind to MusicService
                    MusicServiceManager.bindService(this)

                    // Start the MusicService
                    val serviceIntent = Intent(this, MusicService::class.java)
                    startService(serviceIntent)

                    UserManager.getInstance().fetchAndSetCurrentUser(it)
                    {
                        NotificationsManager.getInstance().createNotificationChannel(this)

                        val homeIntent = Intent(this, HomeActivity::class.java)

                        //add logged in boolean to shared preferences
                        startActivity(homeIntent)
                        finish()
                    }

                }
        }
        else {
            Handler().postDelayed(Runnable {

                NotificationsManager.getInstance().createNotificationChannel(this)
                startActivity(Intent(this@MainActivity, LoginOrSignupActivity::class.java))

                finish()
            }, MainActivity.SPLASH_DELAY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicServiceManager.unbindService(this)
    }

    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 seconds
    }

}