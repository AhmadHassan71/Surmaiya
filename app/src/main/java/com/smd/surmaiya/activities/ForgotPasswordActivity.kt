package com.smd.surmaiya.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var sendButton: Button
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initializeViews()
        setUpOnClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        sendButton = findViewById(R.id.sendButton)
        loginTextView = findViewById(R.id.loginTextView)
    }

    private fun setUpOnClickListeners() {
        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            Navigator.navigateToActivity(this,ResetPasswordActivity::class.java)
        }

        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)
        }
    }

}
