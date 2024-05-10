package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.ManagerClasses.MusicServiceManager
import com.smd.surmaiya.R

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MusicServiceManager.bindService(this) {
            val songUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/surmaiya.appspot.com/o/Albums%2F-NxT2KMTSCLHqgBYpBCj%2F5a9cc003-ad95-4e95-b157-1cdc94946806%2FSongs%2F06ff2cb6-3f1f-403c-ac03-b0e5cd98bb9d.mp3?alt=media&token=ec0729e0-66a9-4fa9-b0c4-64103ec619fa")
            MusicServiceManager.playSong(songUri)
        }

        BottomNavigationHelper(this).loadFragment(HomeFragment())
        BottomNavigationHelper(this).setUpBottomNavigation()


        val musicPlayer = findViewById<View>(R.id.music_player)
        musicPlayer.setOnClickListener {
            val songUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/surmaiya.appspot.com/o/Albums%2F-NxT2KMTSCLHqgBYpBCj%2F5a9cc003-ad95-4e95-b157-1cdc94946806%2FSongs%2F06ff2cb6-3f1f-403c-ac03-b0e5cd98bb9d.mp3?alt=media&token=ec0729e0-66a9-4fa9-b0c4-64103ec619fa")
            MusicServiceManager.playSong(songUri)
            showPlayerBottomSheetDialog()
        }
    }

    private fun showPlayerBottomSheetDialog() {
        val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
        playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)
    }


    override fun onDestroy() {
        super.onDestroy()
        MusicServiceManager.unbindService(this)
    }
}