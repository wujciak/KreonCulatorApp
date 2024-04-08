package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class UserSelect : AppCompatActivity() {

    private lateinit var newUserButton: Button
    private lateinit var goToMealButton: Button
    private lateinit var selectUserSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_select)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Select User"
        newUserButton = findViewById(R.id.newUserButton)
        goToMealButton = findViewById(R.id.goToMealButton)
        selectUserSpinner = findViewById(R.id.selectUserSpinner)
        val items = arrayOf("None")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        selectUserSpinner.adapter = adapter
    }

    private fun setupListeners() {
        newUserButton.setOnClickListener {
            val intent = Intent(this, CreateUser::class.java)
            startActivity(intent)
        }

        goToMealButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
