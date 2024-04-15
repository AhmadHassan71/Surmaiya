package com.smd.surmaiya.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.R

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        val signUpButton = findViewById<TextView>(R.id.signUpTextView)
        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordTextView)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}