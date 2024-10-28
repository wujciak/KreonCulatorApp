package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R
import com.google.firebase.auth.FirebaseAuth

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

        loginButtonSignIn.setOnClickListener{
            logInRegisterUser()
        }

        registerTextViewClickableSignIn.setOnClickListener {
            registerClick(registerTextViewClickableSignIn)
        }
    }

    private fun initializeViews() {
        supportActionBar?.title = "Sign In"
        inputEmailSignIn = findViewById(R.id.inputEmailSignIn)
        inputPasswordSignIn = findViewById(R.id.inputPasswordSignIn)
        loginButtonSignIn = findViewById(R.id.loginButtonSignIn)
        registerTextViewClickableSignIn = findViewById(R.id.registerTextViewClickableSignIn)
    }

    fun registerClick(view: TextView) {
        when (view.id) {
            R.id.registerTextViewClickableSignIn -> {
                val intent = Intent(this, Register::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputEmailSignIn.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, "Please fill your email field!", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(inputPasswordSignIn.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, "Please fill your password field!", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun logInRegisterUser() {
        if (validateLoginDetails()) {
            val email = inputEmailSignIn.text.toString().trim { it <= ' ' }
            val password = inputPasswordSignIn.text.toString().trim { it <= ' ' }

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "You have been logged in successfully.", Toast.LENGTH_LONG).show()

                        goToMenuActivity()
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun goToMenuActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()

        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }
}