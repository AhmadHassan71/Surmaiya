package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.R

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        BottomNavigationHelper(this).loadFragment(HomeFragment())
        BottomNavigationHelper(this).setUpBottomNavigation()

        val musicPlayer = findViewById<View>(R.id.music_player)
        musicPlayer.setOnClickListener {
            showPlayerBottomSheetDialog()
        }
    }

    private fun showPlayerBottomSheetDialog() {
        val playerBottomSheetDialogFragment = PlayerBottomSheetDialogFragment()
        playerBottomSheetDialogFragment.show(supportFragmentManager, playerBottomSheetDialogFragment.tag)
    }



}