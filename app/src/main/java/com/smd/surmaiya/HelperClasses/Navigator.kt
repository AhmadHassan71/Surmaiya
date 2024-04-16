package com.smd.surmaiya.HelperClasses

import android.content.Context
import android.content.Intent
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.activities.MainActivity

class Navigator {


    companion object {
        fun navigateToActivity(context:Context, destination: Class<*>) {
            val intent = Intent(context, destination)
            context.startActivity(intent)
        }
    }
}
