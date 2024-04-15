package com.smd.surmaiya.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.R


class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener{
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
        val loginTextView = findViewById<TextView>(R.id.loginTextView)
        loginTextView.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
