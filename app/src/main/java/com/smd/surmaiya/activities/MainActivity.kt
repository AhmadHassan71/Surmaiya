package com.smd.surmaiya.activities


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.Fragments.EditProfileFragment
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.ManagerClasses.NotificationsManager
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (UserManager.getInstance().getUserLoggedInSP(getSharedPreferences("USER_LOGIN", MODE_PRIVATE))) {

            UserManager.getInstance()
                .getUserEmailSP(getSharedPreferences("USER_LOGIN", MODE_PRIVATE))?.let {

                val intent = Intent(this, HomeActivity::class.java)

                UserManager.getInstance().fetchAndSetCurrentUser(it)
                {
                    //add logged in boolean to shared preferences
                    startActivity(intent)
                    finish()
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

    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 seconds
    }

}
