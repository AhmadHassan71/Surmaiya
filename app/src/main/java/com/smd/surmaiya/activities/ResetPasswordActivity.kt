package com.smd.surmaiya.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.R

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var resetButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initializeViews()
        setUpOnClickListeners()
    }

    private fun initializeViews() {
        resetButton = findViewById(R.id.resetButton)
        loginTextView = findViewById(R.id.loginTextView)
        backButton = findViewById(R.id.backButton)
    }

    private fun setUpOnClickListeners() {
        resetButton.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)
        }

        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

}
