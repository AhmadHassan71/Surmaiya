package com.smd.surmaiya.HelperClasses

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.smd.surmaiya.R

class FragmentNavigationHelper(private val activity: FragmentActivity) {
    fun loadFragment(fragment: Fragment) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        Log.d("BottomNavigationHelper", "Fragment loaded: ${fragment.javaClass.simpleName}")
    }
}