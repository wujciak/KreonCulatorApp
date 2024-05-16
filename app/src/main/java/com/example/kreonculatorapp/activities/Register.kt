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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
        registerButton.setOnClickListener{
            registerUser()
        }
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

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputEmail.text.toString().trim{ it <= ' '}) -> {
                Toast.makeText(this, "Please fill email field!", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(inputName.text.toString().trim{ it <= ' '}) -> {
                Toast.makeText(this, "Please fill name field!", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(inputPassword.text.toString().trim{ it <= ' '}) -> {
                Toast.makeText(this, "Please fill password field!", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(inputPasswordRepeat.text.toString().trim{ it <= ' '}) -> {
                Toast.makeText(this, "Please fill repeat password field!", Toast.LENGTH_SHORT).show()
                false
            }
            inputPassword.text.toString().trim {it <= ' '} != inputPasswordRepeat.text.toString().trim{it <= ' '} -> {
                Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    fun goToLogin() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            val login: String = inputEmail.text.toString().trim {it <= ' '}
            val password: String = inputPassword.text.toString().trim {it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            Toast.makeText(this, "You are registered successfully." + " Your user id is ${firebaseUser.uid}", Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                            // Po zakończeniu rejestracji, przejdź do SignIn Activity
                            val intent = Intent(this, SignIn::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                )
        }
    }
}