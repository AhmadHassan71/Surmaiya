package com.smd.surmaiya.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smd.surmaiya.HelperClasses.Navigator
import com.smd.surmaiya.R
import android.provider.Settings
import androidx.annotation.RequiresApi

class LoginOrSignupActivity : AppCompatActivity() {
    private lateinit var loginTextView: TextView
    private lateinit var signUpButton: Button
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_signup)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_PERMISSIONS)
        }

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)
        }

        initalizeViews()
        setUpOnClickListeners()
    }

    fun initalizeViews() {
        loginTextView = findViewById(R.id.LoginTextView)
        signUpButton = findViewById(R.id.signUpButton)
    }

    fun setUpOnClickListeners() {
        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this, LoginActivity::class.java)
        }

        signUpButton.setOnClickListener {
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permissions were granted, continue with your operation
                } else {
                    // permissions were denied, disable the functionality that depends on these permissions.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}