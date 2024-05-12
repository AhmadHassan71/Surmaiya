package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import androidx.fragment.app.Fragment
import com.smd.surmaiya.Fragments.EditProfileFragment
import com.smd.surmaiya.Fragments.PlaylistSearchFragment
import com.smd.surmaiya.HelperClasses.FragmentNavigationHelper
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.ManagerClasses.FirebaseDatabaseManager
import com.smd.surmaiya.ManagerClasses.NotificationsManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.ManagerClasses.PlaylistManager
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
                        if (intent.getExtras() != null) {
//                    val data = intent.extras?.getBundle("data")
                            val playlistId = intent.getExtras()?.getString("playlistId")
                            val chatType = intent.getExtras()?.getString("chat_type")
                            Log.d("MainActivity", "extras: ${intent.getExtras()}")
                            Log.d("MainActivity", "playlistId: $playlistId")
                            Log.d("MainActivity", "chatType: $chatType")


                            if (chatType == "collaboration") {

                                FirebaseDatabaseManager.getPlaylists { playlists ->
                                    playlists.forEach {pl->
                                        if (pl.playlsitId == playlistId) {
                                            PlaylistManager.addPlaylist(pl)
                                            Log.d("MainActivity", "playlist added: ${PlaylistManager.getPlaylists()}")
                                            val homeIntent = Intent(this, HomeActivity::class.java)
                                            homeIntent.putExtra("playlistId", playlistId)
                                            homeIntent.putExtra("chat_type", chatType)
                                            homeIntent.putExtra("playlist", pl) // Pass the Playlist object
                                            startActivity(homeIntent)
                                            return@forEach
                                        }
                                    }

//                                    BottomNavigationHelper(HomeActivity::class.java).loadFragment(PlaylistSearchFragment())
//                                    homeIntent.putExtra("playlistId", playlistId)
//                                    homeIntent.putExtra("chat_type", chatType)
                                }
                            }


                        } else {
                            NotificationsManager.getInstance().createNotificationChannel(this)

                            val homeIntent = Intent(this, HomeActivity::class.java)

                            //add logged in boolean to shared preferences
                            startActivity(homeIntent)
                            finish()
                        }
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