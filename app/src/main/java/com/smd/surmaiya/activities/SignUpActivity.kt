package com.smd.surmaiya.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var loginTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var signUpButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initializeViews()
        setUpOnClickListeners()
    }

    private fun initializeViews() {
        loginTextView = findViewById(R.id.loginTextView)
        backButton = findViewById(R.id.backButton)
        signUpButton = findViewById(R.id.signUpButton)
    }

    private fun setUpOnClickListeners() {
        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        signUpButton.setOnClickListener {
            Navigator.navigateToActivity(this,HomeActivity::class.java)
        }
    }
}
