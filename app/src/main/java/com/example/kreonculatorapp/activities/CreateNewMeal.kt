package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class CreateNewMeal : AppCompatActivity() {

    private lateinit var newMealEditText: EditText
    private lateinit var addMealButton: Button
    private lateinit var fatEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_meal)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Create New Meal"
        newMealEditText = findViewById(R.id.newMealEditText)
        addMealButton = findViewById(R.id.addMealButton)
        fatEditText = findViewById(R.id.fatEditText)
    }

    private fun setupListeners() {
        addMealButton.setOnClickListener {
            val mealName = newMealEditText.text.toString()
            val fat = fatEditText.text.toString()
            if (mealName.isNotEmpty() && fat.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
