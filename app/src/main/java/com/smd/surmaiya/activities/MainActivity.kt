package com.smd.surmaiya.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smd.surmaiya.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginTextView = findViewById<TextView>(R.id.LoginTextView)
        loginTextView.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}