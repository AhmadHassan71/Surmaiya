package com.smd.surmaiya.activities

import BottomNavigationHelper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smd.surmaiya.Fragments.EditProfileFragment
import com.google.android.material.navigation.NavigationView
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.PlayerBottomSheetDialogFragment
import com.smd.surmaiya.Fragments.SearchFragment
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.adapters.ListItemAdapter
import com.smd.surmaiya.adapters.PlaylistAdapter
import com.smd.surmaiya.adapters.RecentlyPlayedAdapter
import com.smd.surmaiya.adapters.TopGenresAdapter
import com.smd.surmaiya.itemClasses.ListItem
import com.smd.surmaiya.itemClasses.Playlist
import com.smd.surmaiya.itemClasses.Song
import com.smd.surmaiya.itemClasses.TopGenres
import com.smd.surmaiya.R

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        BottomNavigationHelper(this).loadFragment(EditProfileFragment())
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