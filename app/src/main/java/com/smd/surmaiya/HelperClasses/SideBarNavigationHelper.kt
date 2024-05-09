package com.smd.surmaiya.HelperClasses

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.smd.surmaiya.Fragments.SettingsFragment
import com.smd.surmaiya.Fragments.YourUserFragment
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.activities.LoginActivity
import com.smd.surmaiya.itemClasses.UserType

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

     fun setupNavigationView(drawerLayout: DrawerLayout) {
        val navigationView = activity.findViewById<NavigationView>(R.id.nav_view)
        val fragmentHelper = FragmentHelper((activity as AppCompatActivity).supportFragmentManager, activity)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Uncheck all items


            when (menuItem.itemId) {
                R.id.settingsButton -> {
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(SettingsFragment())
                    }, 300)

                    true
                }
                R.id.monthlyRankingsButton -> true
                R.id.popularPLaylistsButton -> {

                    true
                }
                R.id.notificataionsButton -> {

                    true
                }

                else -> false
            }

            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }

    }

    fun openDrawerOnMenuClick(view: View, activity: Activity?) {
        val menuOpener = view.findViewById<ImageView>(R.id.menu_opener)
        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)

        menuOpener.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun prepareUsername(activity: Activity){
        val userName = activity.findViewById<TextView>(R.id.userName)

        if (UserManager.getCurrentUser()?.name == null) {
            userName.text = "User"
            return
        }
        if(UserManager.getCurrentUser()?.userType == UserType.GUEST){
            userName.text = "GUEST USER"
            return
        }
        userName.text = UserManager.getCurrentUser()?.name

        val profilePicture = activity.findViewById<ImageView>(R.id.profilePic)

        Glide.with(activity)
            .load(UserManager.getCurrentUser()?.profilePictureUrl)
            .into(profilePicture)

    }
    private fun handleViewProfile(activity: Activity){
        val viewProfile = activity.findViewById<TextView>(R.id.viewProfile)
        viewProfile.setOnClickListener {
            FragmentHelper((activity as AppCompatActivity).supportFragmentManager, activity).loadFragment(YourUserFragment())
        }
    }

    private fun handleLogout(activity: Activity){
        val logout = activity.findViewById<TextView>(R.id.logoutTextView)
        logout.setOnClickListener {
//            UserManager.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            ContextCompat.startActivity(activity, intent, null)
        }
    }

    fun prepareSideBar(activity: Activity){
        prepareUsername(activity)
        prepareProfilePicture(activity)
        handleViewProfile(activity)
        handleLogout(activity)
    }

    private fun prepareProfilePicture(activity: Activity) {
        val profilePicture = activity.findViewById<ImageView>(R.id.profilePic)

    }


}