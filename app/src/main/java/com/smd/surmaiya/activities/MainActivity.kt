package com.smd.surmaiya.activities

import android.R
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.HelperClasses.CustomProgressBar


class MainActivity : AppCompatActivity() {

    private lateinit var loginTextView: TextView
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.smd.surmaiya.R.layout.player_pop_up)
        val customProgressBar = findViewById<CustomProgressBar>(com.smd.surmaiya.R.id.customProgressBar)
        customProgressBar.setProgress(80f) // Set progress to 50%


//
//        initalizeViews()
//        setUpOnClickListeners()
    }

//    fun initalizeViews()
//    {
//        loginTextView = findViewById(R.id.LoginTextView)
//        signUpButton = findViewById(R.id.signUpButton)
//    }
//
//    fun setUpOnClickListeners()
//    {
//        loginTextView.setOnClickListener{
//            Navigator.navigateToActivity(this, LoginActivity::class.java)
//
//        }
//
//        signUpButton.setOnClickListener{
//            Navigator.navigateToActivity(this, SignUpActivity::class.java)
//        }
//    }


}