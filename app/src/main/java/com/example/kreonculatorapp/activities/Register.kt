package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class Register : AppCompatActivity() {
    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputPasswordRepeat: EditText
    private lateinit var registerButton: Button
    private lateinit var logInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Register"
        inputName = findViewById(R.id.inputName)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputPasswordRepeat = findViewById(R.id.inputPasswordRepeat)
        registerButton = findViewById(R.id.registerButton)
        logInText = findViewById(R.id.logInText)
    }

    private fun setupListeners() {

        registerButton.setOnClickListener {
            val name = inputName.text.toString()
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            val passwordRepeat = inputPasswordRepeat.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordRepeat == password) {
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        logInText.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

    }
}