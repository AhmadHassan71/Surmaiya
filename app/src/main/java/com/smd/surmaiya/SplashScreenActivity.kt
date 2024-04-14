package com.smd.surmaiya

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.splash_screen)
            Handler().postDelayed(Runnable {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }, SPLASH_DELAY)
        }

        companion object {
            private const val SPLASH_DELAY: Long = 2000 // 2 seconds
        }
    }

