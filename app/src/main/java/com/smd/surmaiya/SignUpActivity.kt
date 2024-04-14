package com.smd.surmaiya

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)
        loginTextView.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
