package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kreonculatorapp.R

class SignIn : AppCompatActivity() {
    private lateinit var inputEmailSignIn: EditText
    private lateinit var inputPasswordSignIn: EditText
    private lateinit var loginButtonSignIn: Button
    private lateinit var registerTextViewClickableSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Sign In"
        inputEmailSignIn = findViewById(R.id.inputEmailSignIn)
        inputPasswordSignIn = findViewById(R.id.inputPasswordSignIn)
        loginButtonSignIn = findViewById(R.id.loginButtonSignIn)
        registerTextViewClickableSignIn = findViewById(R.id.registerTextViewClickableSignIn)
    }

    private fun setupListeners() {
        loginButtonSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        registerTextViewClickableSignIn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}