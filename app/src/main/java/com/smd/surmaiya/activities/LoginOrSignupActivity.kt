package com.smd.surmaiya.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.R

class LoginOrSignupActivity : AppCompatActivity() {
    private lateinit var loginTextView: TextView
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_signup)
        val loginTextView = findViewById<TextView>(R.id.LoginTextView)

        initalizeViews()
        setUpOnClickListeners()
    }

    fun initalizeViews()
    {
        loginTextView = findViewById(R.id.LoginTextView)
        signUpButton = findViewById(R.id.signUpButton)
    }

    fun setUpOnClickListeners()
    {
        loginTextView.setOnClickListener{
            startActivity(intent)
            Navigator.navigateToActivity(this, LoginActivity::class.java)

        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }
    }

}