package com.smd.surmaiya.activities


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.ManagerClasses.NotificationsManager
import com.smd.surmaiya.R


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if(UserManager.getInstance().getUserLoggedInSP(getSharedPreferences("USER_LOGIN", MODE_PRIVATE))){
//            Navigator.navigateToActivity(this@MainActivity,HomeActivity::class.java)
//            finish()
//        }
//        else {
        Handler().postDelayed(Runnable {

            NotificationsManager.getInstance().createNotificationChannel(this)
            startActivity(Intent(this@MainActivity, LoginOrSignupActivity::class.java))

            finish()
        }, MainActivity.SPLASH_DELAY)
//        }
    }
    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 seconds
    }

}
