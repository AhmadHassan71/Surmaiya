package com.smd.surmaiya.HelperClasses

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.smd.surmaiya.Fragments.SettingsFragment
import com.smd.surmaiya.R

class SideBarNavigationHelper(private val activity: Activity) {

//    fun setupMenuOpener(view: View) {
//        val menuOpener = view.findViewById<ImageView>(R.id.menu_opener)
//        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
//
//        menuOpener.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//            setupNavigationView(drawerLayout)
//        }
//    }

//    private fun setupNavigationView(drawerLayout: DrawerLayout) {
//        val navigationView = activity.findViewById<NavigationView>(R.id.side_nav)
//        val fragmentHelper = FragmentHelper((activity as AppCompatActivity).supportFragmentManager, activity)
//
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.settingsButton -> true
//                R.id.monthlyRankingsButton -> true
//                R.id.popularPLaylistsButton -> {
//                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        fragmentHelper.loadFragment(SettingsFragment())
//                    }, 300)
//
//                    true
//                }
//                R.id.notificataionsButton -> {
//
//                    true
//                }
//
//                else -> false
//            }
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//    }
}