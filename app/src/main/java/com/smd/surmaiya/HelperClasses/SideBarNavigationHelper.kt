package com.smd.surmaiya.HelperClasses

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.smd.surmaiya.Fragments.ArtistStatsFragment
import com.smd.surmaiya.Fragments.SettingsFragment
import com.smd.surmaiya.Fragments.YourUserFragment
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.ManagerClasses.UserManager.showGuestDialog
import com.smd.surmaiya.R
import com.smd.surmaiya.activities.LoginActivity
import com.smd.surmaiya.activities.LoginOrSignupActivity
import com.smd.surmaiya.activities.NotificationsActivity
import com.smd.surmaiya.activities.PopularPlaylistsActivity
import com.smd.surmaiya.itemClasses.User
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
                    if(UserManager.getCurrentUser()== null){
                        showGuestDialog(activity)
                        return@setNavigationItemSelectedListener false
                    }
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(SettingsFragment())
                    }, 300)

                    true
                }
                R.id.monthlyRankingsButton -> {
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(MonthlyRankingsFragment())
                    }, 300)
                    true
                }
                R.id.popularPLaylistsButton -> {
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(PopularPlaylistsFragment())
                    }, 300)
                    true
                }
                R.id.notificataionsButton -> {
                    if(UserManager.getCurrentUser()== null){
                        showGuestDialog(activity)
                        return@setNavigationItemSelectedListener false
                    }
                    Navigator.navigateToActivity(activity, NotificationsActivity::class.java)

                    true
                }
                R.id.viewArtistStats->{
                    if(UserManager.getCurrentUser()?.userType==UserType.ARTIST){
                        fragmentHelper.closeDrawerWithDelay(drawerLayout, 300)
                        OtherUserManager.addUser(UserManager.getCurrentUser() as User)
                        Handler(Looper.getMainLooper()).postDelayed({
                            fragmentHelper.loadFragment(ArtistStatsFragment())
                        }, 300)

                    }
                    else{
                        CustomToastMaker().showToast(activity, "Only artists can view their stats")
                    }
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
        if(UserManager.getCurrentUser()== null){
            userName.text = "GUEST USER"
            activity.findViewById<ImageView>(R.id.profilePic).visibility = View.GONE
            val viewProfile = activity.findViewById<TextView>(R.id.viewProfile)
            viewProfile.visibility = View.GONE
            return
        }
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

        profilePicture.setOnClickListener{
            val fragmentHelper = FragmentHelper((activity as AppCompatActivity).supportFragmentManager, activity)
            fragmentHelper.closeDrawerWithDelay(activity.findViewById<DrawerLayout>(R.id.drawer_layout), 300)
            Handler(Looper.getMainLooper()).postDelayed({

                fragmentHelper.loadFragment(YourUserFragment())
            }, 300)
        }

    }
    private fun handleViewProfile(activity: Activity){
        val viewProfile = activity.findViewById<TextView>(R.id.viewProfile)
        viewProfile.setOnClickListener {
            val fragmentHelper = FragmentHelper((activity as AppCompatActivity).supportFragmentManager, activity)
            // close the drawer after a delay
            fragmentHelper.closeDrawerWithDelay(activity.findViewById<DrawerLayout>(R.id.drawer_layout), 300)
            Handler(Looper.getMainLooper()).postDelayed({

                fragmentHelper.loadFragment(YourUserFragment())
            }, 300)
        }
    }

    private fun handleLogout(activity: Activity){
        val logout = activity.findViewById<TextView>(R.id.logoutTextView)

        logout.setOnClickListener {

            val mAuth = FirebaseAuth.getInstance()

            UserManager.saveUserLoggedInSP(false, activity.getSharedPreferences("USER_LOGIN",
                AppCompatActivity.MODE_PRIVATE
            ))
            UserManager.saveUserEmailSP("", activity.getSharedPreferences("USER_LOGIN",
                AppCompatActivity.MODE_PRIVATE
            ))
            mAuth.signOut()

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Logout", "Token deleted")
                }
            }


            val intent = Intent(activity, LoginOrSignupActivity::class.java)
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